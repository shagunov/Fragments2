package com.example.fragments2.modules

import android.content.Context
import androidx.room.Room
import com.example.fragments2.UserListApplication
import com.example.fragments2.model.UserDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


//Create class for Injection UserDataBase to Repository
@Module
@InstallIn(SingletonComponent::class)
class RoomDataBaseModule {

    @Singleton
    @Provides
    fun provideUserDB(
        @ApplicationContext application: Context
    ): UserDB = Room.databaseBuilder(application, UserDB::class.java, "user_database")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideUserDao(userDB: UserDB) = userDB.userDao()
}