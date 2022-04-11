package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserForm(val id: String, val fio: String, val phone: String, var email: String, var type: String, var token: String, var refresh: String,  var autoSignIn: Boolean, var fingerPrintSignIn: Boolean) : Parcelable


