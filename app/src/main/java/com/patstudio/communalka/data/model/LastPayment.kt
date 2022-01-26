package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LastPayment (
  val amount: Double?,
  val date: String?
) : Parcelable



