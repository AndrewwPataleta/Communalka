package com.communalka.app.utils

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Android-Package", "com.patstudio.communalka")
            .addHeader("X-Android-Cert", "2154715cb6d2f72fbfe015d20bdef36f9ec73be0")
            .build()
        return chain.proceed(request)
    }


}