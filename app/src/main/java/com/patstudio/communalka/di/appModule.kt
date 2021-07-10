package com.patstudio.communalka.di

import com.example.imagegallery.contextprovider.DefaultDispatcherProvider
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.patstudio.communalka.presentation.ui.splash.SplashViewModel
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<DispatcherProvider> { DefaultDispatcherProvider() }



}