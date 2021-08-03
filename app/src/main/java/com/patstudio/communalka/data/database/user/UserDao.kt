package com.patstudio.communalka.data.database.user

import androidx.room.*
import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
     fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User>

    @Query("SELECT * FROM user WHERE id= :userId LIMIT 1")
    fun getUserById(userId: String): Flow<User>

    @Transaction
    @Query("SELECT * FROM user WHERE lastAuth= :lastAuth")
    fun getLastAuth(lastAuth: Boolean = true): Flow<User>

    @Transaction
    @Query("UPDATE user SET lastAuth = :lastAuth")
    fun updatePreviosAuth(lastAuth: Boolean = false)

    @Query("UPDATE user SET token = :token, refresh = :refresh WHERE id = :userId")
    fun updateToken(token: String, refresh: String, userId: String): Int

    @Transaction
    @Query("UPDATE user SET lastAuth = :lastAuth WHERE id = :userId ")
    fun setLastUpdateUser(userId: String, lastAuth: Boolean = true)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: User) : Long
}