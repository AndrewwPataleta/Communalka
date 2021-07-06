package com.patstudio.communalka.di



import com.patstudio.data.common.utils.Connectivity
import com.patstudio.data.common.utils.ConnectivityImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

  factory<Connectivity> { ConnectivityImpl(androidContext()) }
}