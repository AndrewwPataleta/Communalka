package com.communalka.data.common.utils

import android.content.Context
import android.net.ConnectivityManager
import com.communalka.app.common.utils.Connectivity


class ConnectivityImpl(private val context: Context) : Connectivity {
  
  override fun hasNetworkAccess(): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivityManager.activeNetworkInfo
    return info != null && info.isConnected
  }
}