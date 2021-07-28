package com.patstudio.communalka.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Parcelize
data class UserForm(val id: String, val fio: String, val phone: String, val email: String, var type: String, var token: String) : Parcelable


