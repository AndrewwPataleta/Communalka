package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderCreator (
    var amount: Double,
    var taxAmount: Double,
    var payments: ArrayList<PaymentCreator>
) : Parcelable
