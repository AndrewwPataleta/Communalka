package com.patstudio.communalka.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlacementWrapper (
    val placements: List<Placement>,
) : Parcelable