package com.patstudio.communalka.di

import com.patstudio.communalka.presentation.ui.auth.*
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import com.patstudio.communalka.presentation.ui.main.payment.PaymentPlacementViewModel
import com.patstudio.communalka.presentation.ui.main.payment.PaymentsViewModel
import com.patstudio.communalka.presentation.ui.main.profile.welcome.WelcomeViewModel
import com.patstudio.communalka.presentation.ui.main.profile.HistoryVersionViewModel
import com.patstudio.communalka.presentation.ui.main.profile.PersonalInfoViewModel
import com.patstudio.communalka.presentation.ui.main.profile.UserNotificationViewModel
import com.patstudio.communalka.presentation.ui.main.profile.security.EditPinCodeViewModel
import com.patstudio.communalka.presentation.ui.main.profile.security.EmailEditViewModel
import com.patstudio.communalka.presentation.ui.main.profile.security.EntranceSecurityViewModel
import com.patstudio.communalka.presentation.ui.main.readings.AccrualViewModel
import com.patstudio.communalka.presentation.ui.main.readings.ConsumptionHistoryViewModel
import com.patstudio.communalka.presentation.ui.main.readings.TransmissionReadingListViewModel
import com.patstudio.communalka.presentation.ui.main.readings.TransmissionReadingsViewModel
import com.patstudio.communalka.presentation.ui.main.room.*
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import com.patstudio.communalka.presentation.ui.splash.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { SplashViewModel(get()) }
    viewModel { MainViewModel(get(), get() ,get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegistrationViewModel(get(), get()) }
    viewModel { PinCodeViewModel(get(), get()) }
    viewModel { WelcomeViewModel(get(), get(), get(), get()) }
    viewModel { ConfirmViewModel(get(), get(), get(), get()) }
    viewModel { RestoreViewModel(get(),get()) }
    viewModel { ProfileViewModel(get(),get()) }
    viewModel { AddRoomViewModel(get(), get(), get(),get(), get()) }
    viewModel { PersonalInfoViewModel(get(), get()) }
    viewModel { HistoryVersionViewModel(get(), get()) }
    viewModel { EditRoomViewModel(get(), get(), get(),get(), get()) }
    viewModel { EntranceSecurityViewModel(get(), get()) }
    viewModel { EmailEditViewModel(get(), get()) }
    viewModel { EditPinCodeViewModel(get(), get()) }
    viewModel { PersonalAccountManagementViewModel(get(), get(), get(), get()) }
    viewModel { CreatePersonalAccountViewModel(get(), get(), get(), get()) }
    viewModel { EditPersonalAccountViewModel(get(), get(), get(), get()) }
    viewModel { TransmissionReadingListViewModel(get(), get(), get(), get()) }
    viewModel { TransmissionReadingsViewModel(get(), get(), get(), get()) }
    viewModel { PaymentsViewModel(get(), get(), get(), get()) }
    viewModel { PaymentPlacementViewModel(get(), get()) }
    viewModel { UserNotificationViewModel(get(), get()) }
    viewModel { ConsumptionHistoryViewModel(get(), get(), get()) }
    viewModel { AccrualViewModel(get(), get(), get()) }

}