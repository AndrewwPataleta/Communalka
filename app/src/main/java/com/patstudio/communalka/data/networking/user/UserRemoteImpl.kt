package com.patstudio.communalka.data.networking.user

import com.example.imagegallery.contextprovider.DefaultDispatcherProvider
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.JsonObject
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override suspend fun confirmSmsCode(phone: String, smsCode: String) = withContext(dispatcherProvider.default) {
        val body = JsonObject()
        body.addProperty("target",phone)
        body.addProperty("code",smsCode)
        userService.confirmSms(body)
    }


}