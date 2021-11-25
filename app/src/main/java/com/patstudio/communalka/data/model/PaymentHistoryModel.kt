package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentHistoryModel (
   private val date: String,
   private val receiptNumber: String,
   private val receiptLink: String,
   private val serviceName: String,
   private val placementType: String,
   private val personalAccountNumber: String,
   private val paymentAmount: Float
) : Parcelable

