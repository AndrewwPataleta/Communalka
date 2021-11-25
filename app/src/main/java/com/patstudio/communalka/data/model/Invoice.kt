package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Invoice (
  var balance: Double,
  var penalty: Double,
  var supplier: String,
  var service: String,
  var percentTax: Double,
  var shopId: String
) : Parcelable

