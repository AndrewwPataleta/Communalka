package com.patstudio.communalka.data.networking.user

import com.google.gson.JsonElement
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

interface UserRemote {

    suspend fun login(phone: String): APIResponse<JsonElement>

    suspend fun sendCode(phone: String): APIResponse<JsonElement>

    suspend fun registration(fio: String, phone: String, email: String): APIResponse<JsonElement>

    suspend fun confirmSmsCode(phone: String, smsCode: String): APIResponse<JsonElement>

    suspend fun updateEmail(email: String): APIResponse<JsonElement>

    suspend fun registrationWithCode(fio: String,phone: String, email: String, smsCode: String): APIResponse<JsonElement>

    suspend fun removePlacement(placementId: String): Response


}