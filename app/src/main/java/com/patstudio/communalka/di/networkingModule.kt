package com.patstudio.communalka.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.data.networking.CommunalkaApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = BuildConfig.API_HOST

val networkingModule = module {
  single { GsonConverterFactory.create() as Converter.Factory }
  single { HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY) as Interceptor }

  single {
    OkHttpClient.Builder().apply {
      if (BuildConfig.DEBUG) addInterceptor(get())
          .callTimeout(10, TimeUnit.SECONDS)
          .addInterceptor(ChuckerInterceptor(get()))
    }.build()
  }

  single {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(get())
        .addConverterFactory(get())
        .build()
  }

  single { get<Retrofit>().create(CommunalkaApi::class.java) }
}