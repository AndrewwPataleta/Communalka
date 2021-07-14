package com.patstudio.communalka.data.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfirmSmsParams(val phone: String) : Parcelable