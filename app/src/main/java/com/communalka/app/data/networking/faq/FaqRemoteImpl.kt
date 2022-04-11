package com.communalka.app.data.networking.dadata

import com.communalka.app.common.contextprovider.DispatcherProvider
import kotlinx.coroutines.withContext

class FaqRemoteImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val faqService: FaqService
): FaqRemote {

    override suspend fun getVideo(playlist: String, key: String) = withContext(dispatcherProvider.default) {
        faqService.getVideoFaq("snippet", playlist, key, 30)
    }

}