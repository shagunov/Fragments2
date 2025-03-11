package com.example.fragments2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fragments2.model.User
import com.example.fragments2.model.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _userList = MutableStateFlow(emptyList<User>())
    val userList = _userList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while(true){
                repository.getUsers()
                    .flowOn(Dispatchers.IO)
                    .collectLatest { users ->
                        _userList.update { users }
                    }
            }
        }
    }

    private val _navigationEvent = MutableStateFlow<UserListNavEvent?>(null)
    val navigationEvent = _navigationEvent.asStateFlow()

    fun deleteUser(position: Int) {
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteUser(position)
        }
    }

    fun editUser(id: Int) {
        _navigationEvent.tryEmit(UserListNavEvent.EditUser(id))
    }

    fun seeUserDetails(id: Int) {
        _navigationEvent.tryEmit(UserListNavEvent.UserDetails(id))
    }

    fun addUser() {
        _navigationEvent.tryEmit(UserListNavEvent.NewUser)
    }

    fun completeNavigation(){
        _navigationEvent.tryEmit(null)
    }
}

sealed class UserListNavEvent {
    data class EditUser(val userID: Int): UserListNavEvent()
    data object NewUser: UserListNavEvent()
    data class UserDetails(val userID: Int): UserListNavEvent()
}