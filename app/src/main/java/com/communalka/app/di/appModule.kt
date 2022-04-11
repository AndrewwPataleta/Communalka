package com.communalka.app.di

import com.communalka.app.common.contextprovider.DefaultDispatcherProvider
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import org.koin.dsl.module

val appModule = module {

    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<Gson> { Gson() }


}