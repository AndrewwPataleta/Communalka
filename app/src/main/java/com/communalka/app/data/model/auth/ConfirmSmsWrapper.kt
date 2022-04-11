package com.communalka.app.data.model.auth

import android.os.Parcelable
import com.communalka.app.data.model.UserForm
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfirmSmsWrapper(val consumer: UserForm, val tokens: Tokens) : Parcelable


