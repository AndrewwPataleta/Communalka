package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PersonalCounter (

   var name: String,
   var counterNumber: String,
) : Parcelable