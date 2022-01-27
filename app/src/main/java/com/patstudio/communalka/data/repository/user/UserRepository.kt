package com.patstudio.communalka.data.repository.user

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.data.database.user.UserDao
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.networking.user.UserRemote
import com.patstudio.data.common.utils.Connectivity
import kotlinx.coroutines.flow.*
import okhttp3.Response
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


    fun getConsumer(): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.getConsumer()
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

    fun getFaq(): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.getFaq()
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

    fun getVideoFaqKey(): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.getVideoFaqKey()
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

    fun sendCode(value: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.sendCode(value)
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

    fun getAccount(accountId: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.getAccount(accountId)
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

    fun updateConsumer(consumer: Consumer): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.updateConsumer(consumer)
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

    fun updateGcm(gcm: Gcm): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.updateGsm(gcm)
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

    fun deleteAccount(accountId: String): Flow<Result<Any>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.deleteAccount(accountId)
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

    fun createPersonalAccount(number: String, fio: String, supplier: String, service: String, placementId: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.createPersonalAccount(number, fio, supplier, service, placementId)
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

    fun createMeter(title: String, serial_number: String, value: String, accountId: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.createMeterForAccount(title, serial_number, value, accountId)
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

    fun editMeter(title: String, serial_number: String, value: String?, meterId: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.editMeterForAccount(title, serial_number, value, meterId)
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

    fun getSuppliers(service: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.getSuppliers()
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
                    throwable.printStackTrace()
                    emit(Result.Error(throwable))
                }
            }
        }
    }

    fun getServices(): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.getServices()
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
                    throwable.printStackTrace()
                    emit(Result.Error(throwable))
                }
            }
        }
    }

    fun getMeters(accountId: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.getMeters(accountId)
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

    fun removeMeter(meterId: String): Flow<Result<Any>>  = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.removeMeter(meterId)
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

    suspend fun saveUserLocal(user: User): Long {
        return dao.saveUser(user)
    }

    suspend fun saveUserLocalWithRemote(user: User): Long {
        remote.updateFio(user.name)
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

     fun setPinCode(pinCode: String) {
        sharedPreferences.edit().putString("pinCode", pinCode).apply()
    }


     fun getCurrentPinCode(): String {
        return sharedPreferences.getString("pinCode", "")!!
    }

    fun getCurrentFbToken(): String {
        return sharedPreferences.getString("fbToken", "")!!
    }


    fun setCurrentFbToken(fbToken: String){
        sharedPreferences.edit().putString("fbToken", fbToken).apply()
    }

    suspend fun updatePreviosAuthUser() {
        return dao.updatePreviosAuth()
    }

    suspend fun updatePinCode(userId: String, pincode: String) {
        return dao.updatePinCode(userId, pincode)
    }

    suspend fun updateShowTooltip(userId: String, showPlacementTooltip: Boolean) {
        return dao.updateShowPlacementTooltip(userId, showPlacementTooltip)
    }

    fun getUserById(userId: String): User  {
        return dao.getUserById(userId)
    }

    fun getUsers(): List<User>  {
        return dao.getUsers()
    }


    fun getLastAuthUser(): User  {
        return dao.getLastAuth()
    }

    fun updateAuthSignIn(available: Boolean, userId: String)  {
         dao.updateAuthSignIn(available, userId)
    }

    fun updateFingerPrintAvailable(available: Boolean, userId: String)  {
        dao.updateFingerPrintAvailable(available, userId)
    }

    fun logoutAll()  {
        sharedPreferences.edit().putString("currentToken", null).apply()
        sharedPreferences.edit().putString("currentRefreshToken", null).apply()
        return dao.updatePreviosAuth(false)
    }

    fun updateEmail(email: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.updateEmail(email)
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

    fun createOrder(orderCreator: OrderCreator): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.createOrder(orderCreator)
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


    fun removeRoom(placementId: String): Flow<Result<Any>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val user = remote.removePlacement(placementId)
                emit(Result.success(user))
            }
        } catch (throwable: Exception) {
            throwable.printStackTrace()
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