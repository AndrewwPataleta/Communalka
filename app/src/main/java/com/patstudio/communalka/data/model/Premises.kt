package com.patstudio.communalka.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "premises")
@Parcelize
data class Premises(@PrimaryKey val id: String, val name: String, val address: String, val fioOwner: String, val idOwner: String, val totalSpace: Float, val livingSpace: Float, val imageType: String,  val imagePath: String,  val firstAttach: Boolean = false) : Parcelable


