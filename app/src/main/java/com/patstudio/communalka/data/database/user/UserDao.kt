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

    @Transaction
    @Query("SELECT * FROM user WHERE id= :userId LIMIT 1")
    fun getUserById(userId: String): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: User) : Long
}