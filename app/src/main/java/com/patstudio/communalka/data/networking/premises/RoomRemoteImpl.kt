package com.patstudio.communalka.data.networking.premises

import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Room
import kotlinx.coroutines.withContext

class RoomRemoteImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val premisesService: PremisesService
): RoomRemote {


    override suspend fun getRoomByUserId(userId: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("userId",userId)
        premisesService.getPremisesByUserId(body)
    }

    override suspend fun getRooms() = withContext(dispatcherProvider.io) {
        premisesService.getPremises()
    }

    override suspend fun sendRoom(room: Room) = withContext(dispatcherProvider.io) {
        val body = JsonObject()

        premisesService.addPlacement(room)
    }

    override suspend fun updateRoom(room: Room) = withContext(dispatcherProvider.io)  {
        var jsonObject: JsonObject = JsonObject()
        jsonObject.addProperty("id",room.id)
        jsonObject.addProperty("consumer",room.consumer)
        jsonObject.addProperty("name",room.name)
        jsonObject.addProperty("total_area",room.totalArea)
        jsonObject.addProperty("living_area",room.livingArea)
        jsonObject.addProperty("address",room.address)
        jsonObject.addProperty("created_date",room.createdDate)

        premisesService.updatePlacement(jsonObject, room.id)
    }


    override suspend fun getActualApiKey() = withContext(dispatcherProvider.default) {
        premisesService.getDaDataApiKey()
    }

}