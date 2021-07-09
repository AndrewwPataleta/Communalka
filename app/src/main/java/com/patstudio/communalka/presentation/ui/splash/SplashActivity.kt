package com.patstudio.communalka.presentation.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.presentation.ui.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModel<SplashViewModel>()



    private fun initObservers() {

        viewModel.users.observe(this) { result ->

            when (result) {

                is Result.Loading -> {

                }
                is Result.Success -> {

                }
                is Result.Empty -> {
                  startActivity(Intent(applicationContext,MainActivity::class.java))
                }
                is Result.Error -> {

                }
            }
        }

//        viewModel.images.observe(this) { result ->
//            Log.d("SplashActivity","login result "+result)
//            when (result) {
//
//                is com.patstudio.communalka.data.model.Result.Loading -> {
//
//                }
//                is com.patstudio.communalka.data.model.Result.Success -> {
//
//                }
//                is com.patstudio.communalka.data.model.Result.Empty -> {
//
//                }
//                is com.patstudio.communalka.data.model.Result.Error -> {
//
//                }
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initObservers()
    }


}