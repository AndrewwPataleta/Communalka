package com.patstudio.communalka.data.networking.dadata

import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.withContext

class FaqRemoteImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val faqService: FaqService
): FaqRemote {

    override suspend fun getVideo(playlist: String, key: String) = withContext(dispatcherProvider.default) {
        faqService.getVideoFaq("snippet", playlist, key)
    }

}