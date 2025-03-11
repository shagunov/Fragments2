package com.example.fragments2.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragments2.R
import com.example.fragments2.databinding.FragmentUserListBinding
import com.example.fragments2.presentation.user_list_adapter.UserListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserListFragment : Fragment() {

    private var binding: FragmentUserListBinding? = null
    private var adapterDiffUtil: UserListAdapter? = null

    private val viewModel: UserListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserListBinding.inflate(inflater, container, false)
        binding?.let{ bind -> with(bind) {

            adapterDiffUtil = UserListAdapter(
                onItemClickListener = { id , _ -> viewModel.seeUserDetails(id) },
                onMenuInvocationListener = { id, view ->
                    val popup = PopupMenu(requireContext(), view)
                    popup.inflate(R.menu.manage_user_menu)
                    popup.setOnMenuItemClickListener { item ->
                        when(item.itemId){
                            R.id.editUser -> {
                                viewModel.editUser(id)
                                true
                            }
                            R.id.deleteUser -> {
                                Snackbar.make(root, "Подтвердить удаление", Snackbar.LENGTH_LONG)
                                    .setAction("Да") {
                                        viewModel.deleteUser(id)
                                    }
                                    .show()
                                true
                            }
                            else -> false
                        }
                    }
                    popup.show()
                }
            )

            userList.apply {
                adapter = adapterDiffUtil
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                ))
            }

            addUserButton.setOnClickListener {
                viewModel.addUser()
            }

            val navigator = findNavController()

            lifecycleScope.launch {
                viewModel.userList.collect {
                    adapterDiffUtil?.submitList(it)
                }
            }

            lifecycleScope.launch {
                viewModel.navigationEvent.collect{
                    when(it){
                        is UserListNavEvent.NewUser -> {
                            //navigate to UserEditFragment with param -1
                            val action = UserListFragmentDirections.actionUserListFragmentToUserEditFragment(0)
                            navigator.navigate(action)
                            viewModel.completeNavigation()
                        }
                        is UserListNavEvent.EditUser -> {
                            //navigate to UserEditFragment with param it.position
                            val action = UserListFragmentDirections.actionUserListFragmentToUserEditFragment(it.userID)
                            navigator.navigate(action)
                            viewModel.completeNavigation()
                        }
                        is UserListNavEvent.UserDetails -> {
                            //navigate to UserDetailsFragment with param it.position
                            val action = UserListFragmentDirections.actionUserListFragmentToUserDetailsFragment(it.userID)
                            navigator.navigate(action)
                            viewModel.completeNavigation()
                        }
                        null -> {}
                    }
                }
            }
        }}

        return binding?.root
    }
}