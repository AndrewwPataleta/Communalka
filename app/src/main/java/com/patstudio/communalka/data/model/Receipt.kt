package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Receipt (
  val url: String,
  val number : String
) : Parcelable

