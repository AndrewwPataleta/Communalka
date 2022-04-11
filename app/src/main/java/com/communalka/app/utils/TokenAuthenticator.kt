package com.communalka.app.utils

import android.content.SharedPreferences
import android.util.Log
import com.communalka.app.BuildConfig
import com.communalka.app.data.networking.user.UserService
import okhttp3.*

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TokenAuthenticator(private val sharedPreferences: SharedPreferences): Authenticator {

    val okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .followRedirects(false)
        .authenticator(this)
        .followSslRedirects(false)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_HOST)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(UserService::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        return if (isRequestRequiresAuth(response)) {
            val request = response.request()
            authenticateRequestUsingFreshAccessToken(request, retryCount(request) + 1)
        } else {
            Log.d("TokenAuthenticator", "null return")
            null
        }
    }

    private fun retryCount(request: Request): Int =
        request.header("RetryCount")?.toInt() ?: 0

    @Synchronized
    private fun authenticateRequestUsingFreshAccessToken(
        request: Request,
        retryCount: Int
    ): Request? {
        if (retryCount > 2) return null


            sharedPreferences.getString("currentRefreshToken", "")?.let { lastSavedAccessToken ->

                Log.d("TokenAuthenticator", "current refresh token ${lastSavedAccessToken}")
                Log.d("TokenAuthenticator", "current access token ${sharedPreferences.getString("currentToken", "")}")

                val resp = service.refreshToken(sharedPreferences.getString("currentRefreshToken", "")!!).execute()

                Log.d("TokenAuthenticator", "resp ${resp.code()}")

                if (resp.isSuccessful) {

                    val tokens = resp?.body()?.data?.asJsonObject?.get("tokens")

                    sharedPreferences.edit().putString("currentToken", tokens?.asJsonObject?.get("access")!!.asString).apply()
                    sharedPreferences.edit().putString("currentRefreshToken", tokens?.asJsonObject?.get("refresh")!!.asString).apply()

                    Log.d("TokenAuthenticator", "updated refresh token ${sharedPreferences.getString("currentRefreshToken", "")!!}")
                    Log.d("TokenAuthenticator", "updated access token ${sharedPreferences.getString("currentToken", "")}")



                    return getNewRequest(request, retryCount, tokens?.asJsonObject?.get("access")!!.asString)

                } else {
                    return null
                }
            }

        return null
    }

    private fun getNewRequest(request: Request, retryCount: Int, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer " + accessToken)
            .header("RetryCount", "$retryCount")
            .build()
    }

    private fun isRequestRequiresAuth(response: Response): Boolean {
        val header = response.request().header("Authorization")
        return header != null && header.startsWith("Bearer ")
    }


}