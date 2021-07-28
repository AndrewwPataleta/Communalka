package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class APIResponse<out T>(val status: String, val message: String, val data: T?)