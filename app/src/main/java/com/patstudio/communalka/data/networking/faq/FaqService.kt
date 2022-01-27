package com.patstudio.communalka.data.networking.dadata

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import retrofit2.http.*

interface FaqService {

    @GET("playlistItems")
    suspend fun getVideoFaq(@Query("part") part: String, @Query("playlistId") playlistId: String, @Query("key") key: String): JsonElement

}