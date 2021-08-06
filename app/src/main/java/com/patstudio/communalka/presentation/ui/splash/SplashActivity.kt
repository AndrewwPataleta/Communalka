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
                    Log.d("SplashActivity", result.data.toString())
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                    finish()
                }
                is Result.Empty -> {
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                    finish()
                }
                is Result.Error -> {

                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initObservers()
    }

}