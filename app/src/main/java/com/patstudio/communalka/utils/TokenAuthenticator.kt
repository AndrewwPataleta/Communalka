package com.patstudio.communalka.utils

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.networking.user.UserService
import kotlinx.coroutines.runBlocking
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
        .followSslRedirects(false)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_HOST)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(UserService::class.java)

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "response "+response.request().toString())

        val refreshToken = sharedPreferences.getString("currentRefreshToken", "")
        val updatedToken = getNewToken(refreshToken!!)
        return updatedToken?.let {
            Log.d("TokenAuthenticator ", "new token " + updatedToken)
            response.request().newBuilder().header("Authorization", "Bearer " + it.toString())
                .build()
        }
    }

    private fun getNewToken( refreshToken: String): String? {
        Log.d("TokenAuthenticator ", "get token func")

        val resp = service.refreshToken(refreshToken).execute()

        Log.d("TokenAuthenticator", "resp is successful "+resp.isSuccessful)

        if (resp.isSuccessful) {

            val tokens = resp?.body()?.data?.asJsonObject?.get("tokens")
            Log.d("TokenAuthenticator ", "tokens "+tokens)

            sharedPreferences.edit().putString("currentToken", tokens?.asJsonObject?.get("access")!!.asString).apply()
            sharedPreferences.edit().putString("currentRefreshToken", tokens?.asJsonObject?.get("refresh")!!.asString).apply()


            Log.d("TokenAuthenticator ", "token return"+tokens?.asJsonObject?.get("access")!!.asString.toString())

            return tokens?.asJsonObject?.get("access")!!.asString
        } else {
            return null
        }


    }
}