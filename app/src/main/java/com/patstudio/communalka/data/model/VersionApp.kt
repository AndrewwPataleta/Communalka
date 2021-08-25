package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VersionApp (
    public val number: String,
    val date: String,
    val description: String,
    var opened: Boolean
) : Parcelable
