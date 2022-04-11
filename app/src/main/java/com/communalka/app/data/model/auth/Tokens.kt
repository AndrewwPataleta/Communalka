package com.communalka.app.data.model.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tokens(var access: String, val refresh: String) : Parcelable


