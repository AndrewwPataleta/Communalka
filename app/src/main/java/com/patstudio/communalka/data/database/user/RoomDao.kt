package com.patstudio.communalka.data.database.user

import androidx.room.*
import com.patstudio.communalka.data.model.Premises
import com.patstudio.communalka.data.model.Room
import com.patstudio.communalka.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Query("SELECT * FROM premises")
    fun getAllUsers(): Flow<List<Premises>>

//    @Query("SELECT * FROM premises ")
//    fun getForFirstAuth(firstAuth: Boolean = true): Flow<List<Premises>>

    @Transaction
    @Query("SELECT * FROM premises WHERE premises.idOwner=:userId")
    fun getAllUsersById(userId: String): Flow<List<Premises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePremises(premises: Premises) : Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRoomLocal(room: Room) : Long

    @Transaction
    @Query("SELECT * FROM premises WHERE idOwner= :ownerId")
    fun getUserPremises(ownerId: String): List<Premises>

    @Transaction
    @Query("SELECT * FROM room WHERE consumer= :ownerId")
    fun getUserRooms(ownerId: String): List<Room>

    @Transaction
    @Query("SELECT * FROM room WHERE firstSave= :firstInit LIMIT 1")
    fun getFirstInitRoom(firstInit: Boolean): Room

    @Transaction
    @Query("DELETE FROM room WHERE firstSave= :firstInit ")
    fun removeFirstInitRoom(firstInit: Boolean)


    @Query("UPDATE room SET id=:idRoom, consumer=:consumerId, firstSave=:firstSaveNew WHERE firstSave =:firstSaveOld ")
    fun updateFirstInitRoom(idRoom: String, consumerId: String, firstSaveNew: Boolean, firstSaveOld: Boolean)
}