package com.patstudio.communalka.data.networking.premises

import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.Room
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

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

    override suspend fun getMetersForPlacement(placementId: String) = withContext(dispatcherProvider.io) {
        premisesService.getPlacementInvoice(placementId)
    }

    override suspend fun getMetersByAccount(id: String)  = withContext(dispatcherProvider.io)  {
        premisesService.getMetersByAccount(id)
    }

    override suspend fun getPlacementInvoice(placement: Placement) = withContext(dispatcherProvider.io) {
        premisesService.getPlacementInvoice(placement.id)
    }

    override suspend fun getPlacementDetail(id: String) = withContext(dispatcherProvider.io) {
        premisesService.getPlacementDetail(id)
    }

    override suspend fun getMeterHistory(id: String) = withContext(dispatcherProvider.io) {
        premisesService.getMeterHistory(id)
    }

    override suspend fun getMeterPdf(id: String) = withContext(dispatcherProvider.io)  {
        premisesService.getMeterPdf(id)
    }

    override suspend fun getAccrual(id: String) = withContext(dispatcherProvider.io) {
        premisesService.getAccrual(id)
    }

    override suspend fun getAccount(id: String) = withContext(dispatcherProvider.io)  {
        premisesService.getAccount(id)
    }

    override suspend fun sendRoom(room: Room) = withContext(dispatcherProvider.io) {
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
        jsonObject.addProperty("fio",room.fio)
        jsonObject.addProperty("created_date",room.createdDate)
        premisesService.updatePlacement(jsonObject, room.id)
    }

    override suspend fun getOrderList(
        dateGte: String?,
        dateLte: String?,
        placement: List<String>?,
        services: List<String>?,
        suppliers: List<String>?
    ) = withContext(dispatcherProvider.io)  {
        premisesService.getListOrder(dateGte, dateLte, placement, services, suppliers)
    }


    override suspend fun getActualApiKey() = withContext(dispatcherProvider.default) {
        premisesService.getDaDataApiKey()
    }

    override suspend fun getPlacementPersonalAccount(placement: Placement) = withContext(dispatcherProvider.default) {
        premisesService.getListAccountForPlacement(placement.id)
    }

    override suspend fun getPlacementServices(placement: Placement) = withContext(dispatcherProvider.default) {
        premisesService.getServicesForPlacement(placement.id)
    }

}