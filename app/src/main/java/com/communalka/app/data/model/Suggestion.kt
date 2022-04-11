package com.communalka.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Suggestion(val value: String, val unresticted_value: String, val data: SuggestionData) : Parcelable


