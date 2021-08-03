package com.patstudio.communalka.di



import com.patstudio.communalka.data.networking.premises.PremisesRemote
import com.patstudio.communalka.data.repository.premises.DaDataRepository
import com.patstudio.communalka.data.repository.premises.PremisesRepository
import com.patstudio.communalka.data.repository.user.UserRepository


import com.patstudio.data.common.utils.Connectivity
import com.patstudio.data.common.utils.ConnectivityImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
  factory<UserRepository> { UserRepository(get(),get(),get(),get(),get(named("securePrefs"))) }
  factory<PremisesRepository> { PremisesRepository(get(),get(),get(),get()) }
  factory<DaDataRepository> { DaDataRepository(get(),get(),get(),get(), get(named("securePrefs"))) }
  factory<Connectivity> { ConnectivityImpl(androidContext()) }


}


