package com.communalka.app.data.networking.dadata

import com.communalka.app.data.model.ParentYoutube
import retrofit2.http.*

interface FaqService {

    @GET("playlistItems")
    suspend fun getVideoFaq(@Query("part") part: String, @Query("playlistId") playlistId: String, @Query("key") key: String, @Query("maxResults") maxResults: Int): ParentYoutube

}