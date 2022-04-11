package com.communalka.app.di



import com.communalka.app.data.repository.premises.DaDataRepository
import com.communalka.app.data.repository.premises.RoomRepository
import com.communalka.app.data.repository.user.FaqRepository
import com.communalka.app.data.repository.user.UserRepository


import com.communalka.data.common.utils.Connectivity
import com.communalka.data.common.utils.ConnectivityImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
  factory<UserRepository> { UserRepository(get(),get(),get(),get(),get(named("securePrefs"))) }
  factory<RoomRepository> { RoomRepository(get(),get(),get(),get()) }
  factory<DaDataRepository> { DaDataRepository(get(),get(),get(),get(), get(named("securePrefs"))) }
  factory<FaqRepository> { FaqRepository(get(),get(),get(),get(), get(named("securePrefs"))) }

  factory<Connectivity> { ConnectivityImpl(androidContext()) }


}


