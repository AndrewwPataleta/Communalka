package com.patstudio.communalka.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Service

class IconUtils {

    public lateinit var services: List<Service>

    companion object {
        val instance = IconUtils()
    }

    fun getServiceIcon(name: String, context: Context, imageView: ImageView) {

        lateinit var drawable: Drawable

        services.map {

            if (it.name.compareTo(name) == 0) {
                GlideToVectorYou
                    .init()
                    .with(context)
                    .load(Uri.parse(it.icon), imageView)
            }
        }

    }


}