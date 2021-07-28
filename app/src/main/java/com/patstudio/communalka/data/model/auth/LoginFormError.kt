package com.patstudio.communalka.data.model.auth

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Parcelize
data class LoginFormError(val fio: List<String>, val phone: List<String>, val email: List<String>) : Parcelable


