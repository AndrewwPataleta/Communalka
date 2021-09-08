package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account (
   val id: String,
   val number: String,
   val message: String,
   val fio: String,
   val active: Boolean?,
   val discount: String?,
   val number_people: String?,
   val balance: String?,
   val placement: String?,
   val supplier: String?,
   val service: String?,
   val consumer: String?
) : Parcelable

