package com.patstudio.communalka.data.networking.dadata

import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.google.gson.JsonObject
import kotlinx.coroutines.withContext

class DaDataRemoteImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val daDataService: DaDataService
): DaDataRemote {

    private val LANGUAGE = "ru"
    private val COUNT = 10

    override suspend fun getSurface(query: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("query",query)
//        body.addProperty("count",COUNT)
//        body.addProperty("language",LANGUAGE)
        daDataService.getSuggestions(body)
    }

}