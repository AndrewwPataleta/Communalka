package com.patstudio.communalka.data.networking.dadata

import com.google.gson.JsonElement
import com.patstudio.communalka.data.model.APIResponse

interface FaqRemote {

    suspend fun getVideo(playlist: String, key: String): JsonElement

}