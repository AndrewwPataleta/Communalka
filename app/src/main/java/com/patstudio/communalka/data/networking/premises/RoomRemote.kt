package com.patstudio.communalka.data.networking.premises

import com.google.gson.JsonElement
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.Premises
import com.patstudio.communalka.data.model.Room
import retrofit2.http.Query

interface RoomRemote {

    suspend fun getRoomByUserId(userId: String): APIResponse<JsonElement>

    suspend fun getRooms(): APIResponse<JsonElement>

    suspend fun getMetersForPlacement(placementId: String): APIResponse<JsonElement>

    suspend fun getMetersByAccount(id: String): APIResponse<JsonElement>

    suspend fun getPlacementInvoice(placement: Placement): APIResponse<JsonElement>

    suspend fun getPlacementDetail(id: String): APIResponse<JsonElement>

    suspend fun getMeterHistory(id: String): APIResponse<JsonElement>

    suspend fun getAccrual(id: String): APIResponse<JsonElement>

    suspend fun sendRoom(room: Room): APIResponse<JsonElement>

    suspend fun updateRoom(room: Room): APIResponse<JsonElement>

    suspend fun getOrderList(dateGte: String?,  dateLte: String?, placement: List<String>?, services: List<String>?, suppliers: List<String>?): APIResponse<JsonElement>

    suspend fun getActualApiKey(): APIResponse<JsonElement>

    suspend fun getPlacementPersonalAccount(placement: Placement): APIResponse<JsonElement>

    suspend fun getPlacementServices(placement: Placement): APIResponse<JsonElement>

}