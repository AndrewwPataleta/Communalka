package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentHistoryDetailModel (
    val id: String,
    val account: String,
    val amount: Double,
    val taxAmount: Double

) : Parcelable

