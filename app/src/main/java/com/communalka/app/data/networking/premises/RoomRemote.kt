package com.communalka.app.data.networking.premises

import com.google.gson.JsonElement
import com.communalka.app.data.model.APIResponse
import com.communalka.app.data.model.Placement
import com.communalka.app.data.model.Room
import okhttp3.ResponseBody

interface RoomRemote {

    suspend fun getRoomByUserId(userId: String): APIResponse<JsonElement>

    suspend fun getRooms(): APIResponse<JsonElement>

    suspend fun getMetersForPlacement(placementId: String): APIResponse<JsonElement>

    suspend fun getMetersByAccount(id: String): APIResponse<JsonElement>

    suspend fun getPlacementInvoice(placement: Placement): APIResponse<JsonElement>

    suspend fun getPlacementDetail(id: String): APIResponse<JsonElement>

    suspend fun getMeterHistory(id: String): APIResponse<JsonElement>

    suspend fun getServices(): APIResponse<JsonElement>

    suspend fun getMeterPdf(id: String): ResponseBody

    suspend fun getAccrual(id: String): APIResponse<JsonElement>

    suspend fun getAccount(id: String): APIResponse<JsonElement>

    suspend fun sendRoom(room: Room): APIResponse<JsonElement>

    suspend fun updateRoom(room: Room): APIResponse<JsonElement>

    suspend fun getOrderList(dateGte: String?,  dateLte: String?, placement: List<String>?, services: List<String>?, suppliers: List<String>?): APIResponse<JsonElement>

    suspend fun getActualApiKey(): APIResponse<JsonElement>

    suspend fun getPlacementPersonalAccount(placement: Placement): APIResponse<JsonElement>

    suspend fun getPlacementServices(placement: Placement): APIResponse<JsonElement>

}