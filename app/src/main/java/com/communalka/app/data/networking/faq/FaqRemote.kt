package com.communalka.app.data.networking.dadata

import com.communalka.app.data.model.ParentYoutube

interface FaqRemote {

    suspend fun getVideo(playlist: String, key: String): ParentYoutube

}