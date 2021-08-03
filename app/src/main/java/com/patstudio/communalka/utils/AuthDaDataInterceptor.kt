package com.patstudio.communalka.utils

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.NonNull
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthDaDataInterceptor (private val sharedPreferences: SharedPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getUserCredentials()
        Log.d("AuthDaDataInterceptor", token.toString())
        if (token != null) {
            val request = newRequestWithAccessToken(chain.request(), token)
            return chain.proceed(request)
        } else {
            return chain.proceed(chain.request())
        }
    }

    private fun getUserCredentials(): String? {
       return sharedPreferences.getString("currentDaDataToken", null)
    }

    private fun newRequestWithAccessToken(@NonNull request: Request, @NonNull accessToken: String): Request {
        return if (request.header("Authorization") == null) {
            request.newBuilder()
                .header("Authorization", "Token "+accessToken)
                .build()
        } else {
            request
        }
    }
}