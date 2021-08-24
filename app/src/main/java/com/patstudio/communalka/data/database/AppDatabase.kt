package com.patstudio.communalka.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.patstudio.communalka.data.database.user.RoomDao
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.database.user.UserDao
import com.patstudio.communalka.data.model.Premises
import com.patstudio.communalka.data.model.Room

@Database(entities = [User::class, Premises::class, Room::class], version = 4, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun premisesDao(): RoomDao

}