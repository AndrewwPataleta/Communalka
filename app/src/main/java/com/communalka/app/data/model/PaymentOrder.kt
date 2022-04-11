package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentOrder (
     var orderNumber: Int,
     var communalkaShopId: Int,
     var amount: Double
) : Parcelable
