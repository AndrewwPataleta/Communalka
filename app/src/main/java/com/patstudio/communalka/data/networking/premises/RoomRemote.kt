package com.patstudio.communalka.data.networking.premises

import com.google.gson.JsonElement
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Premises
import com.patstudio.communalka.data.model.Room

interface RoomRemote {

    suspend fun getRoomByUserId(userId: String): APIResponse<JsonElement>

    suspend fun getRooms(): APIResponse<JsonElement>

    suspend fun sendRoom(room: Room): APIResponse<JsonElement>

    suspend fun getActualApiKey(): APIResponse<JsonElement>

}