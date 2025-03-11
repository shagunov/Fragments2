package com.example.fragments2.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fragments2.model.User
import com.example.fragments2.model.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val repository: UserRepository,
    private val state: SavedStateHandle): ViewModel() {

    private val _userDetailFlow = MutableStateFlow<User?>(null)
    val userDetailFlow = _userDetailFlow.asStateFlow()

    val id = state.get<Int>("UserID") ?: -1

    init{
        viewModelScope.launch(Dispatchers.IO){
            _userDetailFlow.update{ repository.getUserById(id) }
        }
    }
}