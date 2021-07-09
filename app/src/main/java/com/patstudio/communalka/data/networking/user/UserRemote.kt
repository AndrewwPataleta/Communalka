package com.patstudio.communalka.data.networking.user

import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

interface UserRemote {

    suspend fun login(username: String, password: String): User

}