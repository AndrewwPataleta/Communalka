package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.data.model.ConfirmSmsParams
import com.patstudio.communalka.data.repository.user.UserRepository
import isValidPhoneNumber
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import startCoroutineTimer

class ConfirmViewModel(private val userRepository: UserRepository): ViewModel() {

    private var smsCode = ""
    private lateinit var phone: String
    private lateinit var timer: Job
    private var TIME_FOR_REPEAT_SMS_SEND = 60
    private val availableSendSms: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()


    private fun startTimer() {
         timer = startCoroutineTimer(delayMillis = 0, repeatMillis = 1000) {
               Log.d("ConfirmSmsViewModel", "tick")

        }
        timer.start()
    }

    fun setPhone(phone: String) {
        availableSendSms.postValue(true)
        this.phone = phone;
     //   startTimer()
    }

    fun setSmsCode(smsCode: String) {
        this.smsCode = smsCode
        if (this.smsCode.length == 6) {
            confirmSmsCode()
        }
    }

    private fun confirmSmsCode() {
        viewModelScope.launch {
            userRepository.confirmSmsCode(phone, smsCode)
                .onStart {
                    progressPhoneSending.postValue(true)

                }
                .catch {
                    Log.d("ConfirmViewModel", it.localizedMessage)
                }
                .collect {

                }
        }
    }


    fun getProgressSmsCodeSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

    fun repeatSendSms() {
        availableSendSms.postValue(false)
    }

    fun getAvailableSendSms(): MutableLiveData<Boolean> {
        return availableSendSms
    }

}