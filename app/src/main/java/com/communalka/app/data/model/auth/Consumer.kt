package com.communalka.app.data.model.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Consumer(val access: String, val refresh: String) : Parcelable


