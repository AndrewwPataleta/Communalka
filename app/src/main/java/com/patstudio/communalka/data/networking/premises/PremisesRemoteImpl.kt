package com.patstudio.communalka.data.networking.premises

import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Premises
import com.patstudio.communalka.data.model.Room
import kotlinx.coroutines.withContext

class PremisesRemoteImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val premisesService: PremisesService
): PremisesRemote {


    override suspend fun getPremisesByUserId(userId: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("userId",userId)
        premisesService.getPremisesByUserId(body)
    }

    override suspend fun getPremises() = withContext(dispatcherProvider.default) {
        premisesService.getPremises()
    }

    override suspend fun savePremises(room: Room) = withContext(dispatcherProvider.default) {
        val body = JsonObject()

        premisesService.addPlacement(room)
    }

    override suspend fun getActualApiKey() = withContext(dispatcherProvider.default) {
        premisesService.getDaDataApiKey()
    }

}