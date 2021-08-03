package com.patstudio.communalka.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val preferencesModule = module {
    single(named("settingsPrefs")) { provideSettingsPreferences(androidApplication()) }
    single(named("securePrefs")) { provideSecurePreferences(androidApplication()) }
}

private const val PREFERENCES_FILE_KEY = "settings_preferences"
private const val SECURE_PREFS_FILE_KEY = "secure_preferences"

private fun provideSettingsPreferences(app: Application): SharedPreferences =
    app.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

private fun provideSecurePreferences(app: Application): SharedPreferences =
    app.getSharedPreferences(SECURE_PREFS_FILE_KEY, Context.MODE_PRIVATE)