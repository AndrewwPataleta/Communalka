package com.patstudio.communalka.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.patstudio.communalka.data.database.user.PremisesDao
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.database.user.UserDao
import com.patstudio.communalka.data.model.Premises

@Database(entities = [User::class, Premises::class], version = 2, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun premisesDao(): PremisesDao

}