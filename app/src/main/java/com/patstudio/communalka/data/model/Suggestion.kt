package com.patstudio.communalka.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

@Parcelize
data class Suggestion(val value: String, val unresticted_value: String, val data: SuggestionData) : Parcelable


