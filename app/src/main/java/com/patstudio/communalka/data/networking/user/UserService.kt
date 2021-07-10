package com.patstudio.communalka.data.networking.user

import com.patstudio.communalka.data.model.User
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {

    @GET("/login")
    suspend fun login(@Query("phone") phone: String): User

}