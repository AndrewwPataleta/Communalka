package com.communalka.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.communalka.app.data.database.user.RoomDao
import com.communalka.app.data.model.User
import com.communalka.app.data.database.user.UserDao
import com.communalka.app.data.model.Premises
import com.communalka.app.data.model.Room

@Database(entities = [User::class, Premises::class, Room::class], version = 10, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun premisesDao(): RoomDao

}