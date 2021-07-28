package com.patstudio.communalka.data.model.auth

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patstudio.communalka.data.model.UserForm
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Parcelize
data class ConfirmSmsWrapper(val consumer: UserForm, val tokens: Tokens) : Parcelable


