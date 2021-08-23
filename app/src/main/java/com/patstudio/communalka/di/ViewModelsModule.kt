package com.patstudio.communalka.di

import com.patstudio.communalka.presentation.ui.auth.*
import com.patstudio.communalka.presentation.ui.main.room.AddRoomViewModel
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import com.patstudio.communalka.presentation.ui.main.WelcomeViewModel
import com.patstudio.communalka.presentation.ui.main.profile.PersonalInfoViewModel
import com.patstudio.communalka.presentation.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegistrationViewModel(get(), get()) }
    viewModel { PinCodeViewModel(get(), get()) }
    viewModel { WelcomeViewModel(get(), get(), get(), get()) }
    viewModel { ConfirmViewModel(get(), get(), get(), get()) }
    viewModel { RestoreViewModel(get(),get()) }
    viewModel { ProfileViewModel(get(),get()) }
    viewModel { AddRoomViewModel(get(), get(), get(),get(), get()) }
    viewModel { PersonalInfoViewModel(get(), get()) }

}