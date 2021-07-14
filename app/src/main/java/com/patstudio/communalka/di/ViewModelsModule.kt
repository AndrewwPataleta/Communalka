package com.patstudio.communalka.di

import com.patstudio.communalka.presentation.ui.auth.ConfirmViewModel
import com.patstudio.communalka.presentation.ui.auth.LoginViewModel
import com.patstudio.communalka.presentation.ui.auth.PinCodeViewModel
import com.patstudio.communalka.presentation.ui.auth.RegistrationViewModel
import com.patstudio.communalka.presentation.ui.main.WelcomeViewModel
import com.patstudio.communalka.presentation.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel { PinCodeViewModel(get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { ConfirmViewModel(get()) }

}