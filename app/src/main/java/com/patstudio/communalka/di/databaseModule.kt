package com.patstudio.communalka.di

import android.app.Application
import androidx.room.Room
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.data.database.AppDatabase
import com.patstudio.communalka.data.database.user.UserDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private const val DATABASE_NAME = BuildConfig.DATABASE_NAME

val databaseModule = module {


    single { provideAppDatabase(androidApplication()) }


    single { provideUserDao(get()) }

}

private fun provideAppDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, DATABASE_NAME)
        .build()
}

private fun provideUserDao(database: AppDatabase): UserDao {
    return database.userDao()
}
