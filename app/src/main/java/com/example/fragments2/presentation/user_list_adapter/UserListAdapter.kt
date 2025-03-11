package com.example.fragments2.presentation.user_list_adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.fragments2.model.User

class UserListAdapter(
    val onItemClickListener: (userID: Int, view: View) -> Unit = { _,_ -> },
    val onMenuInvocationListener: (userID: Int, view: View) -> Unit = { _,_ -> }
) : ListAdapter<User, UserViewHolder>(UsersDiffCallback()){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder.create(parent)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(currentList[position],
            onItemClickListener,
            onMenuInvocationListener)
    }
}

