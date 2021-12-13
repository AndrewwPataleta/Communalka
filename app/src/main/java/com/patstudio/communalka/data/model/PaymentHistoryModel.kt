package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentHistoryModel (
    val id: String,
    var number : Int,
    var date: String,
    var status: String,
    var amount: Double,
    var taxAmount: Double,
    var placementName: String,
    var payments: ArrayList<PaymentHistoryDetailModel>

) : Parcelable

