package com.patstudio.communalka.data.networking.user

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.User
import okhttp3.Response
import retrofit2.http.*

interface UserService {

    @POST("auth/send_code/")
    suspend fun sendCode(@Body target: JsonObject): APIResponse<JsonElement>

    @POST("auth/login/")
    suspend fun confirmSms(@Body target: JsonObject): APIResponse<JsonElement>

    @POST("auth/login/")
    suspend fun login(@Body target: JsonObject): APIResponse<JsonElement>

    @POST("auth/register/")
    suspend fun registration(@Body target: JsonObject): APIResponse<JsonElement>

    @POST("auth/register/")
    suspend fun registrationWithCode(@Body target: JsonObject): APIResponse<JsonElement>

    @PUT("consumer/")
    suspend fun updateEmail(@Body target: JsonObject): APIResponse<JsonElement>

    @FormUrlEncoded
    @POST("auth/update_token/")
    fun refreshToken(@Field("refresh") refresh: String): retrofit2.Call<APIResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "placement/{id}/", hasBody = true)
    suspend fun removePlacement(@Path("id") id: String): Response

}