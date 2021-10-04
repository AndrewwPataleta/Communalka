package com.patstudio.communalka.data.networking.user

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.User
import okhttp3.Response
import okhttp3.ResponseBody
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

    @PUT("consumer/")
    suspend fun updateFio(@Body fio: JsonObject)

    @FormUrlEncoded
    @POST("auth/update_token/")
    fun refreshToken(@Field("refresh") refresh: String): retrofit2.Call<APIResponse<JsonElement>>

    @HTTP(method = "DELETE", path = "placement/{id}/", hasBody = true)
    suspend fun removePlacement(@Path("id") id: String): ResponseBody

    @HTTP(method = "DELETE", path = "meter/{id}/", hasBody = true)
    suspend fun removeMeter(@Path("id") id: String): ResponseBody

    @HTTP(method = "DELETE", path = "account/{id}/", hasBody = true)
    suspend fun deleteAccount(@Path("id") id: String): ResponseBody

    @GET("account/{account}/")
    suspend fun getAccount(@Path("account") id: String): APIResponse<JsonElement>

    @GET("suppliers/")
    suspend fun getSuppliers(): APIResponse<JsonElement>

    @POST("placement/{id}/account/")
    suspend fun createAccount(@Body target: JsonObject, @Path("id") id: String): APIResponse<JsonElement>

    @POST("account/{id}/meter/")
    suspend fun createMeter(@Body target: JsonObject, @Path("id") id: String): APIResponse<JsonElement>

    @PUT("meter/{id}/")
    suspend fun updateMeter(@Body target: JsonObject,@Path("id") id: String): APIResponse<JsonElement>

    @GET("account/{id}/meter/")
    suspend fun getListMeter(@Path("id") id: String): APIResponse<JsonElement>
}