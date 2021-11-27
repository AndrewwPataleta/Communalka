package com.patstudio.communalka.data.networking.premises

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Room
import retrofit2.http.*

interface PremisesService {

    @GET("placement/")
    suspend fun getPremisesByUserId(@Body target: JsonObject): APIResponse<JsonElement>

    @GET("placement/")
    suspend fun getPremises(): APIResponse<JsonElement>



    @POST("placement/")
    suspend fun addPlacement(@Body room: Room): APIResponse<JsonElement>

    @PUT("placement/{id}/")
    suspend fun updatePlacement(@Body room: JsonObject, @Path("id") id: String): APIResponse<JsonElement>

    @GET("placement/{id}/invoices/")
    suspend fun getPlacementInvoice(@Path("id") id: String): APIResponse<JsonElement>

    @GET("placement/{id}/account/")
    suspend fun getListAccountForPlacement(@Path("id") id: String): APIResponse<JsonElement>

    @GET("placement/{id}/services/")
    suspend fun getServicesForPlacement(@Path("id") id: String): APIResponse<JsonElement>



    @GET("order/")
    suspend fun getListOrder(@Query("date__gte") dateGte: String?, @Query("date__lte") dateLte: String?, @Query("payments__account__placements") placements: List<String>?,
                             @Query("payments__account__service") services: List<String>?, @Query("payments__account__supplier") suppliers: List<String>?): APIResponse<JsonElement>

    @GET("api_keys/dadata/")
    suspend fun getDaDataApiKey(): APIResponse<JsonElement>

}