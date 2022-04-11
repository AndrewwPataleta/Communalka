package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfirmSmsParams(val phone: String, val restore: Boolean) : Parcelable