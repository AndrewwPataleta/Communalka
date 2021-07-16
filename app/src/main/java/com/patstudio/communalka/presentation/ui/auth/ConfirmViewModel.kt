package com.patstudio.communalka.presentation.ui.auth

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.data.model.ConfirmSmsParams
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.repository.user.UserRepository
import isValidPhoneNumber
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.flow.*
import startCoroutineTimer

class ConfirmViewModel(private val userRepository: UserRepository): ViewModel() {

    private var smsCode = ""
    private lateinit var userForm: UserForm
    private lateinit var phone: String
    private lateinit var formType: String

    private var TIME_FOR_REPEAT_SMS_SEND = 60
    private val availableSendSms: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val userFormMutable: MutableLiveData<UserForm> = MutableLiveData()
    private val countDownTimer: MutableLiveData<String> = MutableLiveData()
    private val userMessage: MutableLiveData<String> = MutableLiveData()
    private lateinit var timer: CountDownTimer

    private fun startTimer() {

         timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("ConfirmSmsCode", millisUntilFinished.toString())
                countDownTimer.postValue((millisUntilFinished / 1000 % 60).toString())
            }

            override fun onFinish() {
                availableSendSms.postValue(true)
            }
        }
        timer.start()
    }

    fun setPhone(phone: String) {
        availableSendSms.postValue(false)
        startTimer()
        this.phone = phone;
    }


    private fun login() {
        viewModelScope.launch {
            userRepository.confirmSmsCode(phone, smsCode)
                .onStart {
                    progressPhoneSending.postValue(true)

                }
                .catch {
                    Log.d("ConfirmViewModel", it.localizedMessage)
                }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            userFormMutable.postValue(
                                UserForm(
                                    "",
                                    "",
                                    "",
                                "AUTH"
                                )
                            )
                        }
                        is Result.Error -> {
                            userFormMutable.postValue(
                                UserForm(
                                    "",
                                    "",
                                    "",
                                    "AUTH"
                                )
                            )

//                            userMessage.postValue("Ошибка")
//                            progressPhoneSending.postValue(false)
//                            Log.d("RegistrationViewMode", "Succ")
                        }
                    }
                }
        }
    }

    private fun registration() {
        viewModelScope.launch {
            userRepository.registrationWithCode(userForm.fio, userForm.phone, userForm.email, smsCode)
                .onStart {
                    progressPhoneSending.postValue(true)

                }
                .catch {
                    Log.d("ConfirmViewModel", it.localizedMessage)
                }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            userFormMutable.postValue(UserForm(userForm.fio, userForm.phone, userForm.email, "INSTALL"))
                        }
                        is Result.Error -> {
                            userMessage.postValue("Ошибка")
                            progressPhoneSending.postValue(false)
                            Log.d("RegistrationViewMode", "Succ")
                        }
                    }
                }
        }
    }

    fun setSmsCode(smsCode: String) {
        this.smsCode = smsCode
        if (this.smsCode.length == 6) {
            when (formType) {
                "Login" -> login()
                "Registration" -> registration()
            }

        }
    }

    fun getUserForm(): MutableLiveData<UserForm> {
        return userFormMutable
    }

    fun getUserMessage(): MutableLiveData<String> {
        return userMessage
    }

    fun destoyTimer() {
        timer.cancel()

    }

    fun setUserForm(userForm: UserForm) {
        this.phone = userForm.phone
        this.userForm = userForm
        availableSendSms.postValue(false)
        startTimer()
    }

    fun setFormType(formType: String) {
        this.formType = formType
    }

    fun getCountDownTimer(): MutableLiveData<String> {
       return countDownTimer
    }


    fun getProgressSmsCodeSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

    fun repeatSendSms() {
        startTimer()

        availableSendSms.postValue(false)
    }

    fun getAvailableSendSms(): MutableLiveData<Boolean> {
        return availableSendSms
    }

}