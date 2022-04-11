package com.communalka.app.di

import android.app.Application
import androidx.room.Room
import com.communalka.app.BuildConfig
import com.communalka.app.data.database.AppDatabase
import com.communalka.app.data.database.user.RoomDao
import com.communalka.app.data.database.user.UserDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val DATABASE_NAME = BuildConfig.DATABASE_NAME

val databaseModule = module {

    single { provideAppDatabase(androidApplication()) }
    single { provideUserDao(get()) }
    single { providePremisesDao(get()) }

}

private fun provideAppDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build()
}

private fun provideUserDao(database: AppDatabase): UserDao {
    return database.userDao()
}

private fun providePremisesDao(database: AppDatabase): RoomDao {
    return database.premisesDao()
}
