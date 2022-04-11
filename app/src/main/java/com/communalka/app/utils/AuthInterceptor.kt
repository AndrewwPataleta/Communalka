package com.communalka.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import okhttp3.*

import android.app.ActivityManager

import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import com.communalka.app.data.database.user.UserDao
import com.communalka.app.presentation.ui.splash.SplashActivity


class AuthInterceptor(
    private val sharedPreferences: SharedPreferences,
    private val context: Context,
    private val dao: UserDao,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = getUserCredentials()
        var resp: Response
        if (token != null) {
            val request = newRequestWithAccessToken(chain.request(), token)
            resp = chain.proceed(request)
        } else {
            resp = chain.proceed(chain.request())
        }
         if (resp.code() == 401) {
             logout()
             return resp
        } else {
            return resp
         }
    }

    private fun logout() {
        sharedPreferences.edit().putString("currentToken", null).apply()
        sharedPreferences.edit().putString("currentRefreshToken", null).apply()
        dao.updatePreviosAuth(false)
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)
    }

    private fun getCurrentPackageName(): String {
        val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = am.getRunningTasks(1)
        val componentInfo = taskInfo[0].topActivity
        return componentInfo!!.packageName
    }


    private fun getUserCredentials(): String? {
       return sharedPreferences.getString("currentToken", null)
    }

    private fun newRequestWithAccessToken(@NonNull request: Request, @NonNull accessToken: String): Request {
        return if (request.header("Authorization") == null) {
            request.newBuilder()
                .header("Authorization", "Bearer "+accessToken)
                .build()
        } else {
            request
        }
    }
}