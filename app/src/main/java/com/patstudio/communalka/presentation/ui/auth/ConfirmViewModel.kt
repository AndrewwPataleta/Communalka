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
import com.patstudio.communalka.data.model.*
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
    private val availableSendSms: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val availableEmailSendSms: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val userFormMutable: MutableLiveData<Event<UserForm>> = MutableLiveData()
    private val countDownTimer: MutableLiveData<Event<String>> = MutableLiveData()
    private val smsCodeMutable: MutableLiveData<Event<String>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val congratulation: MutableLiveData<Event<String>> = MutableLiveData()
    private var timer: CountDownTimer? = null
    private var repeatCount = 1

    private fun startTimer() {

         timer?.let {
             it.cancel()
             it.onFinish()
         }
         timer = object: CountDownTimer(TIME_FOR_REPEAT_SMS_SEND.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

                Log.d("ConfirmSmsCode", millisUntilFinished.toString())
                var second = (millisUntilFinished / 1000 % 60)
                var printSecond = ""
                if (second < 10)
                    printSecond = "0"+second.toString()
                else {
                    printSecond = second.toString()
                }
                countDownTimer.postValue(Event("0:"+printSecond))
            }

            override fun onFinish() {
                if (repeatCount+1 > 4) {
                    availableEmailSendSms.postValue(Event(true))
                } else {
                    repeatCount += 1
                    availableSendSms.postValue(Event(true))
                }
            }
        }
        timer?.let {
            if (repeatCount+1 > 4) {
                availableEmailSendSms.postValue(Event(true))
            } else {
                availableSendSms.postValue(Event(false))
                it.start()
            }
        }
    }

    fun setPhone(phone: String) {
        availableSendSms.postValue(Event(false))
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
            val user = userRepository.getUserById(userForm.consumer.id)
            if (user != null) {
                Log.d("ConfirmViewMode", user.toString())
                withContext(dispatcherProvider.io) {
                    userRepository.updateToken(user.token, user.refresh, user.id)
                }
                smsCode = ""
                destoyTimer()
                smsCodeMutable.postValue(Event(smsCode))
                userFormMutable.postValue(Event(userForm.consumer))
            } else {
                Log.d("ConfirmViewMode", "have no exist")
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
            progressPhoneSending.postValue(false)
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

    fun getCongratulation(): MutableLiveData<Event<String>> {
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
        availableSendSms.postValue(Event(false))
        congratulation.postValue(Event(userForm.fio))
        startTimer()
    }

    fun setFormType(formType: String) {
        this.formType = formType
    }

    fun setIsRestore(restore: Boolean) {
        this.restore = restore;
    }

    fun getCountDownTimer(): MutableLiveData<Event<String>> {
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
                .onStart {

                }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            startTimer()
                            Log.d("ConfirmViewModel", "success")
                        }
                        is Result.Error -> {
                            Log.d("ConfirmViewModel", "error")

                        }
                        is Result.ErrorResponse -> {
                            var smsError = gson.fromJson(it.data.data, APIResponse::class.java)
                            Log.d("ConfirmViewModel", "error response "+smsError)
                           // userMessage.postValue(Event(smsError.message))
                            availableEmailSendSms.postValue(Event(true))
                        }
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
                        is Result.Success -> {
                            startTimer()
                            Log.d("ConfirmViewModel", "success")
                        }
                        is Result.Error -> {
                            Log.d("ConfirmViewModel", "error")

                        }
                        is Result.ErrorResponse -> {
                            Log.d("ConfirmViewModel", "error response "+it.toString())
                       //     var smsError = gson.fromJson(it.data.data, APIResponse::class.java)

                            // userMessage.postValue(Event(smsError.message))
                            availableEmailSendSms.postValue(Event(true))
                        }
                    }
                }
        }
    }

    fun repeatSendSms() {

        when (formType) {
            "Login" -> repeatSmsLogin()
            "Registration" -> repeatSmsRegistration()
        }
    }

    fun getAvailableSendSms(): MutableLiveData<Event<Boolean>> {
        return availableSendSms
    }

    fun getAvailableEmailSendSms(): MutableLiveData<Event<Boolean>> {
        return availableEmailSendSms
    }
}