package com.patstudio.communalka.data.networking.premises

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Room
import okhttp3.ResponseBody
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

    @GET("accrual/{id}/")
    suspend fun getAccrual(@Path("id") id: String): APIResponse<JsonElement>

    @GET("account/{id}/")
    suspend fun getAccount(@Path("id") id: String): APIResponse<JsonElement>

    @GET("placement/{id}/")
    suspend fun getPlacementDetail(@Path("id") id: String): APIResponse<JsonElement>

    @GET("meter/{id}/history/")
    suspend fun getMeterHistory(@Path("id") id: String): APIResponse<JsonElement>

    @GET("meter/{id}/history/?pdf=true")
    suspend fun getMeterPdf(@Path("id") id: String): ResponseBody

    @GET("account/{id}/meter/")
    suspend fun getMetersByAccount(@Path("id") id: String): APIResponse<JsonElement>

    @GET("account/{id}/meter/")
    suspend fun getMetersAccount(@Path("id") id: String): APIResponse<JsonElement>

    @GET("placement/{id}/account/")
    suspend fun getListAccountForPlacement(@Path("id") id: String): APIResponse<JsonElement>

    @GET("placement/{id}/services/")
    suspend fun getServicesForPlacement(@Path("id") id: String): APIResponse<JsonElement>


    @GET("services/")
    suspend fun getServices(): APIResponse<JsonElement>

    @GET("order/")
    suspend fun getListOrder(@Query("date__gte") dateGte: String?, @Query("date__lte") dateLte: String?, @Query("payments__account__placements") placements: List<String>?,
                             @Query("payments__account__service") services: List<String>?, @Query("payments__account__supplier") suppliers: List<String>?): APIResponse<JsonElement>

    @GET("api_keys/dadata/")
    suspend fun getDaDataApiKey(): APIResponse<JsonElement>

    @GET("api_keys/faq_playlist/")
    suspend fun getFaqPlaylist(): APIResponse<JsonElement>

}