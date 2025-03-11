package com.example.fragments2.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    suspend fun getUserById(id: Int) : User?{
        return userDao.getById(id)
    }

    fun getUsers() : Flow<List<User>> {
        return userDao.getAll()
    }

    suspend fun addUser(user: User){
        userDao.insertAll(user)
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun deleteUser(id: Int){
        userDao.deleteById(id)
    }
}