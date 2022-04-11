package com.communalka.app.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "settings")
@Parcelize
data class Settings(@PrimaryKey val id: String, val pinCode: String, val needFingerPrint: String, val userId: String) : Parcelable


