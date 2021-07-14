package com.patstudio.communalka.data.networking.user

import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

interface UserRemote {

    suspend fun login(phone: String): User

    suspend fun confirmSmsCode(phone: String, smsCode: String): User

}