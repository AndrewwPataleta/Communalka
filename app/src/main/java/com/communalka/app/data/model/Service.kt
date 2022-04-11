package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Service (
   val id: String,
   var name: String,
   val account: Account,
   val icon: String,
   var selected: Boolean = false
) : Parcelable