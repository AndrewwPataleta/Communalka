package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlacementMeter (
    val id: String,
    val serial_number: String?,
    val title: String,
    val model: String,
    val prev_verification: String,
    val next_verification: String,
    val value: Double,
    val account: String,
    val active: Boolean
) : Parcelable
