package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Placement (
    val id: String,
    val consumer: String,
    val name: String,
    val totalArea: Double,
    val livingArea: Double,
    val address: String,
    val createdDate: String
) : Parcelable