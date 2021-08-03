package com.patstudio.communalka.data.database.user

import androidx.room.*
import com.patstudio.communalka.data.model.Premises
import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface PremisesDao {

    @Query("SELECT * FROM premises")
    fun getAllUsers(): Flow<List<Premises>>

//    @Query("SELECT * FROM premises ")
//    fun getForFirstAuth(firstAuth: Boolean = true): Flow<List<Premises>>

    @Transaction
    @Query("SELECT * FROM premises WHERE premises.idOwner=:userId")
    fun getAllUsersById(userId: String): Flow<List<Premises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePremises(premises: Premises) : Long
}