package com.patstudio.communalka.data.database.user

import androidx.room.Dao
import androidx.room.Query
import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
     fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User>

}