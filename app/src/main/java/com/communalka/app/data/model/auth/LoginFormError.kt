package com.communalka.app.data.model.auth

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginFormError(val fio: List<String>, val phone: List<String>, val email: List<String>) : Parcelable


