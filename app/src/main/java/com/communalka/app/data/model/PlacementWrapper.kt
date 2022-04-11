package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlacementWrapper (
    val placements: ArrayList<Placement>,
) : Parcelable