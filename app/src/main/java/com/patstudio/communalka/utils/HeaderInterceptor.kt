package com.patstudio.communalka.utils

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Android-Package", "com.patstudio.communalka")
            .addHeader("X-Android-Cert", "21:54:71:5C:B6:D2:F7:2F:BF:E0:15:D2:0B:DE:F3:6F:9E:C7:3B:E0")
            .build()
        return chain.proceed(request)
    }


}