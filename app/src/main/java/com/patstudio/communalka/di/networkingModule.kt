package com.patstudio.communalka.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.data.networking.CommunalkaApi
import com.patstudio.communalka.data.networking.dadata.DaDataRemote
import com.patstudio.communalka.data.networking.dadata.DaDataRemoteImpl
import com.patstudio.communalka.data.networking.dadata.DaDataService
import com.patstudio.communalka.data.networking.premises.PremisesRemote
import com.patstudio.communalka.data.networking.premises.PremisesRemoteImpl
import com.patstudio.communalka.data.networking.premises.PremisesService
import com.patstudio.communalka.data.networking.user.UserRemote
import com.patstudio.communalka.data.networking.user.UserRemoteImpl
import com.patstudio.communalka.data.networking.user.UserService
import com.patstudio.communalka.data.repository.user.UserRepository
import com.patstudio.communalka.utils.AuthDaDataInterceptor
import com.patstudio.communalka.utils.AuthInterceptor
import com.patstudio.communalka.utils.HeaderInterceptor
import com.patstudio.communalka.utils.TokenAuthenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = BuildConfig.API_HOST
private const val DADATA_URL = BuildConfig.API_HOST_DADATA

val networkingModule = module {

  single { GsonConverterFactory.create() as Converter.Factory }
  single { HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY) as Interceptor }

  single(named("BASE_HTTP")) {
    OkHttpClient.Builder().apply {
      if (BuildConfig.DEBUG)
           addInterceptor(get())
           .connectTimeout(30, TimeUnit.SECONDS)
           .readTimeout(30, TimeUnit.SECONDS)
           .callTimeout(30, TimeUnit.SECONDS)
           .followRedirects(false)
           .followSslRedirects(false)
            addInterceptor(AuthInterceptor(get(named("securePrefs"))))
           .authenticator(TokenAuthenticator(get(named("securePrefs"))))
          .addInterceptor(ChuckerInterceptor(get()))
    }.build()
  }

    single(named("DADATA_HTTP")) {
        OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG)
                addInterceptor(get())
                .addInterceptor(HeaderInterceptor())
                addInterceptor(AuthDaDataInterceptor(get(named("securePrefs"))))
                .callTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(ChuckerInterceptor(get()))
        }.build()
    }

  single(named("BASE_URL"))  {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(get(named("BASE_HTTP")))
        .addConverterFactory(get())
        .build()
  }

    single(named("DADATA_URL")) {
        Retrofit.Builder()
            .baseUrl(DADATA_URL)
            .client(get(named("DADATA_HTTP")))
            .addConverterFactory(get())
            .build()
    }
  single { get<Retrofit>().create(CommunalkaApi::class.java) }

    single { provideUserService(get(named("BASE_URL"))) }
    single { providePremisesService(get(named("BASE_URL"))) }
    single { provideDaDataService(get(named("DADATA_URL"))) }
    // RedditImageRemote instance
    single<UserRemote> { provideUserRemoteImpl(get(), get()) }
    single<PremisesRemote> { providePremisesRemoteImpl(get(), get()) }
    single<DaDataRemote> { provideDaDataRemoteImpl(get(), get()) }

}

private fun provideUserService(retrofit: Retrofit): UserService {
    return retrofit.create(UserService::class.java)
}

private fun provideDaDataService(retrofit: Retrofit): DaDataService {
    return retrofit.create(DaDataService::class.java)
}

private fun provideUserRemoteImpl(
    service: UserService,
    dispatchers: DispatcherProvider
): UserRemote {
    return UserRemoteImpl(dispatchers,service)
}

private fun provideDaDataRemoteImpl(
    service: DaDataService,
    dispatchers: DispatcherProvider
): DaDataRemote {
    return DaDataRemoteImpl(dispatchers,service)
}

private fun providePremisesService(retrofit: Retrofit): PremisesService {
    return retrofit.create(PremisesService::class.java)
}

private fun providePremisesRemoteImpl(
    service: PremisesService,
    dispatchers: DispatcherProvider
): PremisesRemote {
    return PremisesRemoteImpl(dispatchers,service)
}


