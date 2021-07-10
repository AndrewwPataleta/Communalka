package com.patstudio.communalka

import android.app.Application
import com.patstudio.communalka.di.*
import com.patstudio.communalka.di.databaseModule
import com.patstudio.communalka.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level


class CommunalkaApp: Application() {

    companion object {

        lateinit var instance: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin()
    }

    private fun startKoin() {
        org.koin.core.context.startKoin {
            androidContext(this@CommunalkaApp)
            if (BuildConfig.DEBUG) androidLogger(Level.DEBUG)
            modules(appModules + dataModules + databaseModule+ repositoryModule+ viewModules)
        }
    }

}

val appModules = listOf(appModule)
val dataModules = listOf(networkingModule)
val viewModules = listOf(viewModelsModule)
val databaseModule = listOf(databaseModule)
val repositoryModule = listOf(repositoryModule)