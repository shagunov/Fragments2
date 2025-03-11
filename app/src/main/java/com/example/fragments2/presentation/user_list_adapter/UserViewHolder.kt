package com.example.fragments2.presentation.user_list_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments2.databinding.UserListItemBinding
import com.example.fragments2.model.User

class UserViewHolder(
    private val binding: UserListItemBinding
): RecyclerView.ViewHolder(binding.root){
    private var userID = 0
    // callbacks
    private var onMenuInvocationListener: (userID: Int, view: View) -> Unit = { _, _ -> }
    private var onItemClickListener: (userID: Int, view: View) -> Unit = { _, _ -> }

    fun onBind(user: User?,
               onItemClickListener: (userID: Int, view: View) -> Unit,
               onMenuInvocationListener: (userID: Int, view: View) -> Unit){
        userID = user?.id ?: 0
        with(binding){

            this@UserViewHolder.onMenuInvocationListener = onMenuInvocationListener
            this@UserViewHolder.onItemClickListener = onItemClickListener

            menuButton.setOnClickListener{
                onMenuInvocationListener(userID, menuButton)
            }

            root.setOnClickListener{
                onItemClickListener(userID, root)
            }

            userName.text = user?.name ?: ""
            userLastName.text = user?.lastName ?: ""
            userPhone.text = user?.phoneNumber ?: ""
            userEmail.text = user?.email ?: ""
        }
    }

    companion object {
        fun create(parent: ViewGroup): UserViewHolder = UserListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::UserViewHolder)

    }
}