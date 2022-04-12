package com.communalka.app.data.repository.user

import android.content.SharedPreferences
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.communalka.app.data.model.ParentYoutube
import com.communalka.app.data.model.Result
import com.communalka.app.data.networking.dadata.FaqRemote
import com.communalka.app.common.utils.Connectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FaqRepository(
    private val faqRemote: FaqRemote,
    private val dispatcherProvider: DispatcherProvider,
    private val gson: Gson,
    private val connectivity: Connectivity,
    private val sharedPreferences: SharedPreferences
) {

    fun getVideo(playlist: String, key: String): Flow<Result<ParentYoutube>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = faqRemote.getVideo(playlist, key)
                emit(Result.success(premises))
            }
        } catch (throwable: Exception) {
            throwable.printStackTrace()
            emit(Result.Error(throwable))
        }

    }

}