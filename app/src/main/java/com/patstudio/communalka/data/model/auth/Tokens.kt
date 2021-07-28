package com.patstudio.communalka.data.model.auth

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Parcelize
data class Tokens(var access: String, val refresh: String) : Parcelable


