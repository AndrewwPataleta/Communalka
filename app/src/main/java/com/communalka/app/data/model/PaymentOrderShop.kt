package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.tinkoff.acquiring.sdk.models.Shop

@Parcelize
data class PaymentOrderShop (
     var orderNumber: Int,
     var communalkaShopId: Int,
     var communalkaTax: String,
     var amount: Double,
     var shops: ArrayList<Shop>,
     var services: ArrayList<Pair<Long,String>>?,
     var email: String
) : Parcelable
