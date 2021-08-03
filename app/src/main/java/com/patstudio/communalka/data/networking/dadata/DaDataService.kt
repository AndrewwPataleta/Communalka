package com.patstudio.communalka.data.networking.dadata

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import retrofit2.http.*

interface DaDataService {

    @POST("suggest/address/")
    suspend fun getSuggestions( @Body body: JsonObject): JsonElement

}