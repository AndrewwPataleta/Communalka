package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Placement (
    val id: String,
    val consumer: String,
    var name: String,
    val fio: String,
    val total_area: Double,
    val living_area: Double,
    val address: String,
    var imageType: String,
    var path: String,
    val createdDate: String,
    var isOpened: Boolean = false,
    var selected: Boolean = false,
    var accounts: ArrayList<PlacementAccount>,
    var invoices: ArrayList<Invoice>? = null
) : Parcelable
