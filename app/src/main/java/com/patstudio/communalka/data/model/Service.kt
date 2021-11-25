package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Service (
   val id: String,
   var name: String,
   val account: Account,
   var selected: Boolean = false
) : Parcelable