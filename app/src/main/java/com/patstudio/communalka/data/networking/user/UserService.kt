package com.patstudio.communalka.data.networking.user

import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.User
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @POST("auth/send_code/")
    suspend fun login(@Body target: JsonObject): User

    @POST("auth/login/")
    suspend fun confirmSms(@Body target: JsonObject): User

}