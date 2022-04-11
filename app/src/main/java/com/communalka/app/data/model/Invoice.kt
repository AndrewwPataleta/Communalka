package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Invoice (
  var balance: Double,
  var penalty: Double,
  var penaltyValue: Double? = null,
  var supplier: String,
  var service: String,
  var percentTax: Double,
  var currentTax: Double? = null,
  var shopId: String,
  var selected: Boolean = true
) : Parcelable

