package com.patstudio.communalka.data.networking

import retrofit2.http.GET

interface CommunalkaApi {

    @GET
    fun getHouseType()

}