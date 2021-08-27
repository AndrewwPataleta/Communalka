package com.patstudio.communalka.data.networking.premises

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Room
import retrofit2.http.*

interface PremisesService {

    @GET("placement/")
    suspend fun getPremisesByUserId(@Body target: JsonObject): APIResponse<JsonElement>

    @GET("placement/")
    suspend fun getPremises(): APIResponse<JsonElement>

    @POST("placement/")
    suspend fun addPlacement(@Body room: Room): APIResponse<JsonElement>

    @PUT("placement/{id}/")
    suspend fun updatePlacement(@Body room: JsonObject, @Path("id") id: String): APIResponse<JsonElement>

    @GET("api_keys/dadata/")
    suspend fun getDaDataApiKey(): APIResponse<JsonElement>

}