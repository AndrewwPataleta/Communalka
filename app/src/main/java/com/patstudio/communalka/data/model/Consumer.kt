package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Consumer (
   val id: String,
   val phone: String,
   val email: String,
   var remindIndication: Boolean,
   var remindPay: Boolean,
   var messageRSO: Boolean,
   var personal: Boolean,
   var ad: Boolean,
) : Parcelable

