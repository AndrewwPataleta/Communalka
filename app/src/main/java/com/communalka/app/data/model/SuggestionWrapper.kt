package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SuggestionWrapper(val suggestions: List<Suggestion> ): Parcelable


