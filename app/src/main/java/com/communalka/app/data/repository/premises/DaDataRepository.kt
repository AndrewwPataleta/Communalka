package com.communalka.app.data.repository.premises

import android.content.SharedPreferences
import android.util.Log
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.communalka.app.data.model.Result
import com.communalka.app.data.networking.dadata.DaDataRemote
import com.communalka.app.common.utils.Connectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class DaDataRepository(
    private val daDataRemote: DaDataRemote,
    private val dispatcherProvider: DispatcherProvider,
    private val gson: Gson,
    private val connectivity: Connectivity,
    private val sharedPreferences: SharedPreferences
) {

    fun setCurrentDaDataToken(token: String) {

        sharedPreferences.edit().putString("currentDaDataToken", token).apply()
    }

    fun getSuggestions(query: String): Flow<Result<JsonElement>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = daDataRemote.getSurface(query)
                emit(Result.success(premises))
            }
        } catch (throwable: Exception) {
            throwable.printStackTrace()
            Log.d("DaDataRepository", (throwable is HttpException).toString())
            Log.d("DaDataRepository", "exception "+throwable.localizedMessage)
            emit(Result.Error(throwable))
        }

    }

}