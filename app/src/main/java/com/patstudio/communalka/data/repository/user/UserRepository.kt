package com.patstudio.communalka.data.repository.user

import android.util.Log
import androidx.lifecycle.LiveData
import com.patstudio.communalka.data.database.user.UserDao
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.networking.user.UserRemote
import com.patstudio.data.common.utils.Connectivity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import okhttp3.Response

class UserRepository (
    private val remote: UserRemote,
    private val dao: UserDao,
    private val connectivity: Connectivity
) {

     val users: Flow<Result<List<User>>>
        get() = dao.getAllUsers().map {
            Result.successOrEmpty(it)
        }

     fun login(phone: String): Flow<Result<User>> = flow {
          try {
              if (connectivity.hasNetworkAccess()) {
                  emit(Result.loading())
                  val user = remote.login(phone)
                  emit(Result.success(user))
              }
          } catch (e: Exception) {
              emit(Result.error(e))
          }
     }

    fun confirmSmsCode(phone: String, smsCode: String): Flow<Result<User>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.confirmSmsCode(phone, smsCode)
                emit(Result.success(user))
            }
        } catch (e: Exception) {
            emit(Result.error(e))
        }
    }

}