package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class  PlacementAccount (
    val id: String,
    val consumer: String,
    val service: String,
    val supplierName: String,
    val placement: String,
    val number: String,
    val message: String,
    val active: Boolean,
    val discount: String,
    val number_people: String,
    val balance: String,
    var meters: ArrayList<PlacementMeter>
) : Parcelable
