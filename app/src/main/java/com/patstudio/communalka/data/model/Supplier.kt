package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Supplier (
   val id: String,
   val name: String,
   val service: String,
) : Parcelable