package com.patstudio.communalka.data.repository.user

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.data.database.user.UserDao
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.networking.user.UserRemote
import com.patstudio.data.common.utils.Connectivity
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class UserRepository (
    private val remote: UserRemote,
    private val dao: UserDao,
    private val connectivity: Connectivity,
    private val gson: Gson,
    private val sharedPreferences: SharedPreferences
) {

     val users: Flow<Result<List<User>>>
        get() = dao.getAllUsers().map {
            Result.successOrEmpty(it)
        }

     fun login(value: String): Flow<Result<APIResponse<JsonElement>>> = flow {
          try {
              if (connectivity.hasNetworkAccess()) {
                  emit(Result.loading())
                  val user = remote.login(value)
                  emit(Result.success(user))
              }
          } catch (throwable: Exception) {
              when (throwable) {
                  is IOException ->  emit(Result.error(throwable))
                  is HttpException -> {
                      val errorResponse = convertErrorBody(throwable)
                      emit(Result.errorResponse(errorResponse))
                  }
                  else -> {
                      emit(Result.Error(throwable))
                  }
              }
          }
     }

    fun registration(fio: String, phone: String, email: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.registration(fio,phone,email)
                emit(Result.success(user))
            }
        } catch (throwable: Exception) {
            Log.d("UserRepository", (throwable is HttpException).toString())
            when (throwable) {
                is IOException ->  emit(Result.error(throwable))
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    Log.d("UserRepository", errorResponse.toString())
                    emit(Result.errorResponse(errorResponse))
                }
                else -> {
                    emit(Result.Error(throwable))
                }
            }
        }
    }

    suspend fun saveUser(user: User): Long {
        return dao.saveUser(user)
    }

    fun updateToken(token: String, refresh: String, userId: String) {
        Log.d("UserRepository", "update")
        dao.updateToken(token, refresh, userId)
    }

    suspend fun setLastLoginUser(user: User) {
        sharedPreferences.edit().putString("currentToken", user.token).apply()
        sharedPreferences.edit().putString("currentRefreshToken", user.refresh).apply()
        return dao.setLastUpdateUser(user.id)
    }

    suspend fun updatePreviosAuthUser() {
        return dao.updatePreviosAuth()
    }

    fun getUserById(userId: String): User  {
        return dao.getUserById(userId)
    }

    fun getLastAuthUser(): Flow<User>  {
        return dao.getLastAuth()
    }

    fun logoutAll()  {
        sharedPreferences.edit().putString("currentToken", null).apply()
        sharedPreferences.edit().putString("currentRefreshToken", null).apply()
        return dao.updatePreviosAuth(false)
    }

    fun confirmSmsCode(phone: String, smsCode: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.confirmSmsCode(phone, smsCode)
                emit(Result.success(user))
            }
        } catch (throwable: Exception) {
            when (throwable) {
                is IOException ->  emit(Result.error(throwable))
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    emit(Result.errorResponse(errorResponse))
                }
                else -> {
                    emit(Result.Error(throwable))
                }
            }
        }
    }

    fun registrationWithCode(fio: String,phone: String, email: String, smsCode: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.registrationWithCode(fio, phone,email, smsCode)
                emit(Result.success(user))
            }
        } catch (throwable: Exception) {
            when (throwable) {
                is IOException ->  emit(Result.error(throwable))
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    emit(Result.errorResponse(errorResponse))
                }
                else -> {
                    emit(Result.Error(throwable))
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): APIResponse<JsonElement> {

            val type = object : TypeToken<APIResponse<JsonElement>>() {}.type
            Log.d("UserRepository", "login error body"+throwable.response()?.errorBody()!!)
            return gson.fromJson(throwable.response()?.errorBody()!!.charStream().readText(), type)

    }

}