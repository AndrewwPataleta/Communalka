package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsumptionHistory (
     var period: String,
     var period_string: String,
     var consumption: Int,
     var value: Int,
     var isOpened: Boolean = false,
     var children: ArrayList<ConsumptionHistory>
) : Parcelable

