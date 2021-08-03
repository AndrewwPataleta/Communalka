package com.patstudio.communalka.data.networking.premises

import com.google.gson.JsonElement
import com.patstudio.communalka.data.model.APIResponse
import com.patstudio.communalka.data.model.Premises
import com.patstudio.communalka.data.model.Room

interface PremisesRemote {

    suspend fun getPremisesByUserId(userId: String): APIResponse<JsonElement>

    suspend fun getPremises(): APIResponse<JsonElement>

    suspend fun savePremises(room: Room): APIResponse<JsonElement>

    suspend fun getActualApiKey(): APIResponse<JsonElement>

}