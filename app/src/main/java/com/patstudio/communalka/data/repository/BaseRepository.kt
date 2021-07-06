package com.patstudio.communalka.data.repository


import com.patstudio.communalka.domain.Failure
import com.patstudio.communalka.domain.HttpError
import com.patstudio.data.common.coroutine.CoroutineContextProvider
import com.patstudio.data.common.utils.Connectivity
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


abstract class BaseRepository<T : Any> : KoinComponent {
  private val connectivity: Connectivity by inject()
  private val contextProvider: CoroutineContextProvider by inject()

  protected suspend fun fetchData(dataProvider: suspend () -> com.patstudio.communalka.domain.Result<T>): com.patstudio.communalka.domain.Result<T> {
    return if (connectivity.hasNetworkAccess()) {
      withContext(contextProvider.io) {
        dataProvider()
      }
    } else {
      Failure(HttpError(Throwable("GENERAL_NETWORK_ERROR")))
    }
  }
}