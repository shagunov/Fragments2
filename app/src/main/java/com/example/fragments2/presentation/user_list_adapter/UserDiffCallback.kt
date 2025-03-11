package com.example.fragments2.presentation.user_list_adapter

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.example.fragments2.model.User

class UsersDiffCallback : ItemCallback<User>(){
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}