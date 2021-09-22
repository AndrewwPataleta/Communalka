package com.patstudio.communalka.data.database.user

import androidx.room.*
import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
     fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user")
    fun getUsers(): List<User>

    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User>

    @Query("SELECT * FROM user WHERE id= :userId LIMIT 1")
    fun getUserById(userId: String): User

    @Transaction
    @Query("SELECT * FROM user WHERE lastAuth= :lastAuth")
    fun getLastAuth(lastAuth: Boolean = true): User

    @Transaction
    @Query("UPDATE user SET lastAuth = :lastAuth")
    fun updatePreviosAuth(lastAuth: Boolean = false)

    @Transaction
    @Query("UPDATE user SET token = :token, refresh = :refresh WHERE id = :userId")
    fun updateToken(token: String, refresh: String, userId: String)

    @Query("UPDATE user SET pinCode = :pinCode WHERE id = :userId")
    fun updatePinCode(userId: String,pinCode: String)

    @Transaction
    @Query("UPDATE user SET showPlacementTooltip = :showPlacementTooltip WHERE id = :userId")
    fun updateShowPlacementTooltip(userId: String, showPlacementTooltip: Boolean)

    @Transaction
    @Query("UPDATE user SET autoSignIn = :autoSignIn WHERE id = :userId")
    fun updateAuthSignIn(autoSignIn: Boolean, userId: String)

    @Transaction
    @Query("UPDATE user SET fingerPrintSignIn = :fingerPrintAvailable WHERE id = :userId")
    fun updateFingerPrintAvailable(fingerPrintAvailable: Boolean, userId: String)

    @Transaction
    @Query("UPDATE user SET lastAuth = :lastAuth WHERE id = :userId ")
    fun setLastUpdateUser(userId: String, lastAuth: Boolean = true)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: User) : Long
}