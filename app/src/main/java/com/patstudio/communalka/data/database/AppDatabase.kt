package com.patstudio.communalka.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.database.user.UserDao

@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

}