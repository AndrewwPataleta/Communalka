package com.communalka.app.data.model


data class APIResponse<out T>(val status: String, val message: String, val data: T?)