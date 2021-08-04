package com.patstudio.communalka.presentation.ui.auth

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.ConfirmSmsParams
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
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

class ConfirmViewModel(private val userRepository: UserRepository, private val gson: Gson, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private var smsCode = ""
    private lateinit var userForm: UserForm
    private lateinit var phone: String
    private lateinit var formType: String
    private var restore = false
    private var TIME_FOR_REPEAT_SMS_SEND = 60000
    private val availableSendSms: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val userFormMutable: MutableLiveData<Event<UserForm>> = MutableLiveData()
    private val countDownTimer: MutableLiveData<String> = MutableLiveData()
    private val smsCodeMutable: MutableLiveData<Event<String>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val congratulation: MutableLiveData<String> = MutableLiveData()
    private var timer: CountDownTimer? = null

    private fun startTimer() {

         timer?.let {
             it.cancel()
             it.onFinish()
         }
         timer = object: CountDownTimer(TIME_FOR_REPEAT_SMS_SEND.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("ConfirmSmsCode", millisUntilFinished.toString())
                var second = (millisUntilFinished / 1000 % 60).toString()
                countDownTimer.postValue("0:"+second)
            }

            override fun onFinish() {
                availableSendSms.postValue(true)
            }
        }
        timer?.let {
            it.start()
        }
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
                                    userForm.consumer.refresh = userForm.tokens.refresh
                                    checkExistAndSaveUser(userForm)
                                    progressPhoneSending.postValue(false)
                                }
                            }

                        }
                        is Result.ErrorResponse -> {
                            when(it.data.status) {
                                "fail" -> {
                                    userMessage.postValue(Event(it.data.message))
                                    progressPhoneSending.postValue(false)
                                }
                            }
                        }
                    }
                }
        }
    }


    private fun checkExistAndSaveUser(userForm: ConfirmSmsWrapper) {
        Log.d("ConfirmViewModel", "check exist "+userForm.consumer)
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.getUserById(userForm.consumer.id)
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    if (it != null) {
                    //    userRepository.updateToken(userForm.consumer.token, userForm.consumer.refresh, userForm.consumer.id)
                        smsCode = ""
                        destoyTimer()
                        smsCodeMutable.postValue(Event(smsCode))
                        userFormMutable.postValue(Event(userForm.consumer))
                    } else {
                        var userForSave = User(
                            userForm.consumer.id,
                            userForm.consumer.fio,
                            userForm.consumer.phone,
                            userForm.consumer.email,
                            "",
                            userForm.consumer.token,
                            userForm.consumer.refresh,
                            true
                        )
                        userRepository.saveUser(userForSave)
                        destoyTimer()
                        smsCode = ""
                        smsCodeMutable.postValue(Event(smsCode))
                        userFormMutable.postValue(Event(userForm.consumer))
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
                                    userForm.consumer.refresh = userForm.tokens.refresh
                                    checkExistAndSaveUser(userForm)
                                }
                            }
                        }
                        is Result.ErrorResponse -> {
                            when(it.data.status) {
                                "fail" -> {
                                    var confirmError = gson.fromJson(it.data.data, ConfirmFormError::class.java)
                                    userMessage.postValue(Event(confirmError.code))
                                    progressPhoneSending.postValue(false)
                                }
                            }
                        }
                        is Result.Error -> {
                            userMessage.postValue(Event("Ошибка"))
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

    fun getUserForm(): MutableLiveData<Event<UserForm>> {
        return userFormMutable
    }

    fun getCongratulation(): MutableLiveData<String> {
        return congratulation
    }

    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }


    fun destoyTimer() {
        timer?.let {
            it.cancel()
        }
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

    fun getSmsCode(): MutableLiveData<Event<String>> {
        return smsCodeMutable
    }

    private fun repeatSmsLogin() {
        viewModelScope.launch {
            userRepository.login(phone)
                .onStart {}
                .collect {
                    when (it) {
                        is Result.Success -> { }
                        is Result.Error -> { }
                        is Result.ErrorResponse -> { }
                    }
                }
        }
    }

    private fun repeatSmsRegistration() {
        viewModelScope.launch {
            userRepository.login(phone)
                .onStart {}
                .collect {
                    when (it) {
                        is Result.Success -> { }
                        is Result.Error -> { }
                        is Result.ErrorResponse -> { }
                    }
                }
        }
    }

    fun repeatSendSms() {
        startTimer()
        when (formType) {
            "Login" -> repeatSmsLogin()
            "Registration" -> repeatSmsRegistration()
        }
        availableSendSms.postValue(false)
    }

    fun getAvailableSendSms(): MutableLiveData<Boolean> {
        return availableSendSms
    }

}