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
    var imageType: String,
    var path: String,
    val createdDate: String,
    var isOpened: Boolean = false
) : Parcelable
