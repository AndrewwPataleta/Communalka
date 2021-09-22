package com.patstudio.communalka.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user")
@Parcelize
data class User(@PrimaryKey val id: String, var name: String, val phone: String, var email:String, val pinCode: String, val token: String,  val refresh: String, val lastAuth: Boolean, var photoPath: String,  var autoSignIn: Boolean = false, var fingerPrintSignIn: Boolean = false, var showPlacementTooltip: Boolean = true) : Parcelable


