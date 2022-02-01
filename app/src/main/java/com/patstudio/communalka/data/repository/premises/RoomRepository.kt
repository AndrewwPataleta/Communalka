package com.patstudio.communalka.data.repository.premises

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.data.database.user.RoomDao
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.networking.premises.RoomRemote
import com.patstudio.data.common.utils.Connectivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat

class RoomRepository(
    private val roomRemote: RoomRemote,
    private val roomDao: RoomDao,
    private val connectivity: Connectivity,
    private val gson: Gson
) {

    suspend fun saveLocalPremises(premises: Premises): Long {
        return roomDao.savePremises(premises)
    }

    suspend fun saveRoomLocal(room: Room): Long {
        return roomDao.saveRoomLocal(room)
    }

    suspend fun getUserPremises(userId: String): List<Room> {
        return roomDao.getUserRooms(userId)
    }

    suspend fun getFirstInitRoom(): Room {
        return roomDao.getFirstInitRoom(true)
    }

    suspend fun removeFirstInitRoom() {
         roomDao.removeFirstInitRoom(true)
    }

    suspend fun updateFirstInitRoom(idRoom: String, consumerId: String) {
        roomDao.updateFirstInitRoom(idRoom, consumerId, firstSaveNew = false, firstSaveOld = true)
    }

    suspend fun sendPremises(room: Room): APIResponse<JsonElement> {
        Log.d("RoomRepository", "sub send ")
       
       return  roomRemote.sendRoom(room)
    }


    suspend fun updatePremises(room: Room): APIResponse<JsonElement> {
        Log.d("RoomRepository", "sub send ")
        return  roomRemote.updateRoom(room)
    }

    suspend fun getUserPremises(): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getRooms()
                emit(Result.success(premises))
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

    suspend fun getPlacementDetail(idPlacement: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getPlacementDetail(idPlacement)
                emit(Result.success(premises))
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


    suspend fun getMeterHistory(meterId: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getMeterHistory(meterId)
                emit(Result.success(premises))
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

    suspend fun getMeterPdf(meterId: String): Flow<ResponseBody> = flow {
        val premises = roomRemote.getMeterPdf(meterId)
        emit(premises)
    }


    suspend fun getAccrual(id: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getAccrual(id)
                emit(Result.success(premises))
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

    suspend fun getAccount(id: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getAccount(id)
                emit(Result.success(premises))
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

    suspend fun getMetersForPlacement(placementId: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getMetersForPlacement(placementId)
                emit(Result.success(premises))
            }
        } catch (throwable: Exception) {
            Log.d("PremisesRepository", (throwable is HttpException).toString())
            when (throwable) {
                is IOException ->  emit(Result.error(throwable))
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    Log.d("PremisesRepository", errorResponse.toString())
                    emit(Result.errorResponse(errorResponse))
                }
                else -> {
                    emit(Result.Error(throwable))
                }
            }
        }
    }

    suspend fun getMetersByAccount(account: String): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getMetersByAccount(account)
                emit(Result.success(premises))
            }
        } catch (throwable: Exception) {
            Log.d("PremisesRepository", (throwable is HttpException).toString())
            when (throwable) {
                is IOException ->  emit(Result.error(throwable))
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    Log.d("PremisesRepository", errorResponse.toString())
                    emit(Result.errorResponse(errorResponse))
                }
                else -> {
                    emit(Result.Error(throwable))
                }
            }
        }
    }

    suspend fun getPlacementInvoice(placement: Placement): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getPlacementInvoice(placement)
                emit(Result.success(premises))
            }
        } catch (throwable: Exception) {
            Log.d("PremisesRepository", (throwable is HttpException).toString())
            when (throwable) {
                is IOException ->  emit(Result.error(throwable))
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    Log.d("PremisesRepository", errorResponse.toString())
                    emit(Result.errorResponse(errorResponse))
                }
                else -> {
                    emit(Result.Error(throwable))
                }
            }
        }
    }



    suspend fun getPaymentsHistory(dateGte: String?, dateLte: String?, placement: List<String>?, services: List<String>?, suppliers: List<String>?): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {


                emit(Result.loading())

                val premises = roomRemote.getOrderList(dateGte, dateLte, placement, services, suppliers)
                emit(Result.success(premises))
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


    suspend fun getPlacementPersonalAccount(placement: Placement): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getPlacementPersonalAccount(placement)
                emit(Result.success(premises))
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

    suspend fun getServicesPlacement(placement: Placement): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getPlacementServices(placement)
                emit(Result.success(premises))
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

    suspend fun getActualApiKey(): Flow<Result<APIResponse<JsonElement>>> = flow {
        try {
            if (connectivity.hasNetworkAccess()) {
                emit(Result.loading())
                val premises = roomRemote.getActualApiKey()
                emit(Result.success(premises))
            }
        } catch (throwable: Exception) {
            throwable.printStackTrace()
            Log.d("PremisesRepository", (throwable is HttpException).toString())
            when (throwable) {
                is IOException ->  emit(Result.error(throwable))
                is HttpException -> {
                    val errorResponse = convertErrorBody(throwable)
                    Log.d("PremisesRepository", errorResponse.toString())
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
        return gson.fromJson(throwable.response()?.errorBody()!!.charStream().readText(), type)

    }

}