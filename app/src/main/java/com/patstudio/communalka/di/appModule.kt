package com.patstudio.communalka.di

import com.patstudio.communalka.common.contextprovider.DefaultDispatcherProvider
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import org.koin.dsl.module

val appModule = module {

    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<Gson> { Gson() }


}