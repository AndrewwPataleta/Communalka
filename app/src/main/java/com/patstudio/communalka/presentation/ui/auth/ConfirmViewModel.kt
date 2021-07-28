package com.patstudio.communalka.presentation.ui.auth

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.patstudio.communalka.data.model.ConfirmSmsParams
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.model.auth.ConfirmFormError
import com.patstudio.communalka.data.model.auth.ConfirmSmsFormError
import com.patstudio.communalka.data.model.auth.ConfirmSmsWrapper
import com.patstudio.communalka.data.model.auth.LoginFormError
import com.patstudio.communalka.data.repository.user.UserRepository
import isValidPhoneNumber
import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.flow.*
import startCoroutineTimer

class ConfirmViewModel(private val userRepository: UserRepository, private val gson: Gson): ViewModel() {

    private var smsCode = ""
    private lateinit var userForm: UserForm
    private lateinit var phone: String
    private lateinit var formType: String
    private var restore = false
    private var TIME_FOR_REPEAT_SMS_SEND = 60000
    private val availableSendSms: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val userFormMutable: MutableLiveData<UserForm> = MutableLiveData()
    private val countDownTimer: MutableLiveData<String> = MutableLiveData()
    private val userMessage: MutableLiveData<String> = MutableLiveData()
    private val congratulation: MutableLiveData<String> = MutableLiveData()
    private lateinit var timer: CountDownTimer

    private fun startTimer() {

         timer = object: CountDownTimer(TIME_FOR_REPEAT_SMS_SEND.toLong(), 1000) {
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
                            Log.d("ConfirmSmsModel", it.data.toString())
                            when(it.data.status) {
                                "success" -> {
                                    var userForm: ConfirmSmsWrapper = gson.fromJson(it.data.data, ConfirmSmsWrapper::class.java)
                                    if (restore) {
                                        userForm.consumer.type = "INSTALL"
                                    } else {
                                        userForm.consumer.type = "AUTH"
                                    }

                                    userForm.consumer.token = userForm.tokens.access
                                    userFormMutable.postValue(userForm.consumer)
                                    progressPhoneSending.postValue(false)
                                }
                            }

                        }
                        is Result.ErrorResponse -> {
                            when(it.data.status) {
                                "fail" -> {
                                    userMessage.postValue(it.data.message)
                                    progressPhoneSending.postValue(false)
                                }
                            }
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
                            when(it.data.status) {
                                "success" -> {
                                    var userForm: ConfirmSmsWrapper = gson.fromJson(it.data.data, ConfirmSmsWrapper::class.java)
                                    userForm.consumer.type = "INSTALL"
                                    userForm.consumer.token = userForm.tokens.access
                                    userFormMutable.postValue(userForm.consumer)

                                }
                            }
                        }
                        is Result.ErrorResponse -> {
                            when(it.data.status) {
                                "fail" -> {
                                    var confirmError = gson.fromJson(it.data.data, ConfirmFormError::class.java)
                                    userMessage.postValue(confirmError.code)
                                    progressPhoneSending.postValue(false)
                                }
                            }
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

    fun getCongratulation(): MutableLiveData<String> {
        return congratulation
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
        congratulation.postValue(userForm.fio)
        startTimer()
    }

    fun setFormType(formType: String) {
        this.formType = formType
    }

    fun setIsRestore(restore: Boolean) {
        this.restore = restore;
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