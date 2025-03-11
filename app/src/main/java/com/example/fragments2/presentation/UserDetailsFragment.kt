package com.example.fragments2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.fragments2.databinding.FragmentUserDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    private val viewModel: UserDetailsViewModel by viewModels()
    private var binding: FragmentUserDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        binding?.let{

            lifecycleScope.launch {
                viewModel.userDetailFlow
                    .collect { user ->
                        it.nameTextView.text = user?.name
                        it.lastNameTextView.text = user?.lastName
                        it.phoneNumberTextView.text = user?.phoneNumber
                        it.emailTextView.text = user?.email
                        it.countryTextView.text = user?.country
                        it.cityTextView.text = user?.city
                        it.aboutHimselfTextView.text = user?.himself
                    }
            }
        }

        // Inflate the layout for this fragment
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}