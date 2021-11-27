package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentHistoryDetailModel (
    val id: String,
    val account: String,
    val amount: Double,
    val taxAmount: Double

) : Parcelable

