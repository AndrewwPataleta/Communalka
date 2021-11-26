package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeterHistory (
   var date: String,
   var lastValue: Double,
   var prevValue: Double,
   var consumption: Double
) : Parcelable

