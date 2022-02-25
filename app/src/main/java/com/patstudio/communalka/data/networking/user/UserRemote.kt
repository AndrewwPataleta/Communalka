package com.patstudio.communalka.data.networking.user

import com.google.gson.JsonElement
import com.patstudio.communalka.data.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

interface UserRemote {

    suspend fun login(phone: String): APIResponse<JsonElement>

    suspend fun sendCode(phone: String): APIResponse<JsonElement>

    suspend fun getConsumer(): APIResponse<JsonElement>

    suspend fun getFaq(): APIResponse<JsonElement>


    suspend fun getVideoFaqKey(): APIResponse<JsonElement>

    suspend fun registration(fio: String, phone: String, email: String): APIResponse<JsonElement>

    suspend fun updateFio(fio: String)

    suspend fun confirmSmsCode(phone: String, smsCode: String): APIResponse<JsonElement>

    suspend fun updateEmail(email: String): APIResponse<JsonElement>

    suspend fun updateEmailProfile(email: String): APIResponse<JsonElement>

    suspend fun createOrder(orderCreator: OrderCreator): APIResponse<JsonElement>

    suspend fun updateGsm(gcm: Gcm): APIResponse<JsonElement>

    suspend fun registrationWithCode(fio: String,phone: String, email: String, smsCode: String): APIResponse<JsonElement>

    suspend fun updatePhone(phone: String,code: String): APIResponse<JsonElement>

    suspend fun updateEmail(phone: String,code: String): APIResponse<JsonElement>

    suspend fun removePlacement(placementId: String): Any

    suspend fun getAccount(accountId:  String): APIResponse<JsonElement>

    suspend fun updateConsumer(consumer: Consumer): APIResponse<JsonElement>

    suspend fun deleteAccount(accountId:  String): Any

    suspend fun createPersonalAccount(number: String, fio: String, supplier: String, service: String,placementId: String): APIResponse<JsonElement>

    suspend fun createMeterForAccount(title: String, serial_number: String, value: String, accountId: String): APIResponse<JsonElement>

    suspend fun editMeterForAccount(title: String, serial_number: String, value: String?, meterId: String): APIResponse<JsonElement>

    suspend fun getSuppliers(): APIResponse<JsonElement>

    suspend fun getMeters(accountId: String): APIResponse<JsonElement>

    suspend fun getServices(): APIResponse<JsonElement>

    suspend fun removeMeter(meterId: String): Any


}