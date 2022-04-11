package com.communalka.app.data.networking.dadata

import com.google.gson.JsonElement

interface DaDataRemote {

    suspend fun getSurface(query: String): JsonElement

}