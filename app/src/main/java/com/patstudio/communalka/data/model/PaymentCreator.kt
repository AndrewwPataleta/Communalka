package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentCreator (
     var account: String,
     var amount: Double,
     var taxAmount: Double,
     var shopId: String
) : Parcelable
