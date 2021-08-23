package com.patstudio.communalka.data.networking.user

import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.APIResponse
import kotlinx.coroutines.withContext

class UserRemoteImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val userService: UserService
): UserRemote  {

    override suspend fun login(phone: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("target",phone)
        userService.login(body)
    }

    override suspend fun sendCode(phone: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("target",phone)
        userService.sendCode(body)
    }

    override suspend fun registration(fio: String, phone: String, email: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("fio",fio)
        body.addProperty("phone",phone)
        if (email.length > 0)
            body.addProperty("email",email)
        userService.registration(body)
    }

    override suspend fun confirmSmsCode(phone: String, smsCode: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("target",phone)
        body.addProperty("code",smsCode)
        userService.confirmSms(body)
    }

    override suspend fun registrationWithCode(
        fio: String,
        phone: String,
        email: String,
        smsCode: String
    ) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("fio",fio)
        body.addProperty("phone",phone)
        if (email.length > 0)
            body.addProperty("email",email)
        body.addProperty("code",smsCode)
        userService.registrationWithCode(body)
    }

}