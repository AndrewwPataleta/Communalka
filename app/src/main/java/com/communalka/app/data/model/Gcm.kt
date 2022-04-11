package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Gcm (
  var name: String? = null,
  var registration_id: String,
  var device_id: Int? = null,
  var active: Boolean,
  var cloud_message_type: String = "FCM",
  var application_id: String? = null,

) : Parcelable

