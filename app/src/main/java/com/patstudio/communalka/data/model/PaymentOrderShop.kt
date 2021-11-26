package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.tinkoff.acquiring.sdk.models.Shop

@Parcelize
data class PaymentOrderShop (
     var orderNumber: Int,
     var communalkaShopId: Int,
     var amount: Double,
     var shops: ArrayList<Shop>
) : Parcelable
