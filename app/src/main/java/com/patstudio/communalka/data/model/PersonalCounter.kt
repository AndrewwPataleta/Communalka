package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonalCounter (

   var title: String,
   var value: String,
   var serial_number: String? = "",
   var id: String? = null,
   var account: String? = null,
   var active: Boolean? = null,
   var model: String? = null,

   ) : Parcelable