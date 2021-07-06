package com.patstudio.communalka.data.repository.user

import com.patstudio.communalka.domain.Result
import com.patstudio.communalka.domain.model.UserModel

interface UserRepository {

    suspend fun getCurrentUser(): Result<UserModel>

}