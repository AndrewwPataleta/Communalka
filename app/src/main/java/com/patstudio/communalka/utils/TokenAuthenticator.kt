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

class TokenAuthenticator(private val sharedPreferences: SharedPreferences): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "response "+response.request().toString())

        return runBlocking {

            val refreshToken = sharedPreferences.getString("currentRefreshToken", "")
            Log.d("TokenAuthenticator", "old token "+sharedPreferences.getString("currentToken",""))
            Log.d("TokenAuthenticator", "current refresh token "+refreshToken)
            val updateResponse = getUpdatedToken(refreshToken!!)

            val tokens = updateResponse.data?.asJsonObject?.get("tokens")
            sharedPreferences.edit().putString("currentToken", tokens?.asJsonObject?.get("access").toString()).apply()
            sharedPreferences.edit().putString("currentRefreshToken", tokens?.asJsonObject?.get("refresh").toString()).apply()
            Log.d("TokenAuthenticator", "updated token "+sharedPreferences.getString("currentToken",""))

            val accessToken = sharedPreferences.getString("currentToken","")

            response.request().newBuilder()
                .header("Authorization", "Bearer ${accessToken}")
                .build()

        }
    }

    private suspend fun getUpdatedToken(refreshToken: String): APIResponse<JsonElement> {

        val okHttpClient = OkHttpClient().newBuilder()
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val service = retrofit.create(UserService::class.java)
        val body = JsonObject()
        body.addProperty("refresh",refreshToken)

        Log.d("TokenAuthenticator", "body "+body.toString())
        Log.d("TokenAuthenticator", "service "+service.toString())
        return service.refreshToken(body)

    }

}