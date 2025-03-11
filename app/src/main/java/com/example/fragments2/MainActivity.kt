package com.example.fragments2

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.fragments2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMainBinding.inflate(layoutInflater)

        val navHost = supportFragmentManager.findFragmentById(binding.fragmentUsersView.id) as NavHostFragment
        navController = navHost.navController

        NavigationUI.setupActionBarWithNavController(this, navController)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                when(navController.currentDestination?.id){
                    R.id.userEditFragment -> {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(getText(R.string.confirm_back_unsaved_changes))
                            .setPositiveButton("Да") { _, _ ->
                                navController.navigateUp()
                            }
                            .setNegativeButton("Нет") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    R.id.userListFragment -> {
                        finish()
                    }
                    else -> navController.navigateUp()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        // if the user is on the edit fragment we should call alert dialog to confirm the navigation up
        if (navController.currentDestination?.id == R.id.userEditFragment){
            var confirmDialog = false
            AlertDialog.Builder(this)
                .setTitle(getText(R.string.confirm_back_unsaved_changes))
                .setPositiveButton("Да") { _, _ ->
                    navController.navigateUp()
                    confirmDialog = true
                }
                .setNegativeButton("Нет") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return confirmDialog
        }
        else return navController.navigateUp() || super.onSupportNavigateUp()
    }
}