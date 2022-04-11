package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Faq (
   val question: String,
   val answer: String,
   var opened: Boolean = false,
) : Parcelable

