package com.communalka.app.data.networking.dadata

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.http.*

interface DaDataService {

    @POST("suggest/address/")
    suspend fun getSuggestions( @Body body: JsonObject): JsonElement

}