package com.patstudio.communalka.data.networking.premises

import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonObject
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

    override suspend fun getActualApiKey() = withContext(dispatcherProvider.default) {
        premisesService.getDaDataApiKey()
    }

}