package com.patstudio.communalka.data.networking.dadata

import com.google.gson.JsonElement
import com.patstudio.communalka.data.model.APIResponse

interface DaDataRemote {

    suspend fun getSurface(query: String): JsonElement

}