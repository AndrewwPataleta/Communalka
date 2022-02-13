package com.patstudio.communalka.presentation.ui.auth

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.model.auth.ConfirmFormError
import com.patstudio.communalka.data.model.auth.ConfirmSmsWrapper
import com.patstudio.communalka.data.model.auth.LoginFormError
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ConfirmViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val gson: Gson, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private var smsCode = ""
    private lateinit var userForm: UserForm
    private lateinit var phone: String
    private lateinit var formType: String
    private var restore = false
    private var TIME_FOR_REPEAT_SMS_SEND = 60000
    private val availableSendSms: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val availableEmailSendSms: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val clearSmsForm: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val loginByEmail: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val userFormMutable: MutableLiveData<Event<Pair<UserForm, Boolean>>> = MutableLiveData()
    private val countDownTimer: MutableLiveData<Event<String>> = MutableLiveData()
    private val smsCodeMutable: MutableLiveData<Event<String>> = MutableLiveData()
    private val openDialogEmail: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val userMessageWithoutButton: MutableLiveData<Event<String>> = MutableLiveData()
    private val congratulation: MutableLiveData<Event<String>> = MutableLiveData()
    private var timerSms: CountDownTimer? = null
    private var timerEmail: CountDownTimer? = null
    private var repeatCount = 1
    private var onlyEmail = false

    private fun startTimer() {

            timerSms?.let {
                it.cancel()
                it.onFinish()
            }

            timerSms = object: CountDownTimer(TIME_FOR_REPEAT_SMS_SEND.toLong(), 1000) {
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
                    availableSendSms.postValue(Event(true))
                }
            }
            timerSms?.let {
                availableSendSms.postValue(Event(false))
                it.start()
            }
    }

    fun setPhone(phone: String) {
        availableSendSms.postValue(Event(false))
        startTimer()
        this.phone = phone;
    }


    private fun login() {
        viewModelScope.launch(dispatcherProvider.io) {
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
                                    clearSmsForm.postValue(Event(true))
                                    userMessage.postValue(Event(it.data.message))
                                    progressPhoneSending.postValue(false)
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun sendLocalRoom(room: Room, userForm: ConfirmSmsWrapper) {
        viewModelScope.launch(dispatcherProvider.io) {
            val resp = roomRepository.sendPremises(room).collect {
                when (it) {
                    is Result.Success -> {
                        var placement = gson.fromJson(it.data!!.data!!.asJsonObject.get("placement"), Placement::class.java)
                        roomRepository.updateFirstInitRoom(placement.id, placement.consumer)
                        smsCodeMutable.postValue(Event(smsCode))
                        var currentPinCode = userRepository.getCurrentPinCode()
                        if (currentPinCode.isNullOrEmpty()) {
                            userFormMutable.postValue(Event(Pair(userForm.consumer, true)))
                        } else {
                            userFormMutable.postValue(Event(Pair(userForm.consumer, false)))
                        }

                        progressPhoneSending.postValue(false)
                    }
                    is Result.Error -> {

                    }
                    is Result.ErrorResponse -> {

                    }
                }
            }
        }
    }

    private fun checkExistAndSaveUser(userForm: ConfirmSmsWrapper) {
        Log.d("ConfirmViewModel", "check exist "+userForm.consumer)
        viewModelScope.launch(dispatcherProvider.io) {
            val user = userRepository.getUserById(userForm.consumer.id)
            var userForSave = User(
                userForm.consumer.id,
                userForm.consumer.fio,
                userForm.consumer.phone,
                userForm.consumer.email,
                "",
                userForm.consumer.token,
                userForm.consumer.refresh,
                true,
                ""
            )

            if (user != null) {
                Log.d("ConfirmViewMode", user.toString())
                withContext(dispatcherProvider.io) {
                    userRepository.updateToken(user.token, user.refresh, user.id)
                }
                smsCode = ""
                destoyTimer()
            } else {
                userRepository.saveUserLocal(userForSave)
                userRepository.setLastLoginUser(userForSave)
                destoyTimer()
                smsCode = ""
            }
            withContext(dispatcherProvider.io) {
                val room = roomRepository.getFirstInitRoom()
                if (room != null) {
                    sendLocalRoom(room, userForm)
                } else {
                    smsCodeMutable.postValue(Event(smsCode))
                    val currentPinCode = userRepository.getCurrentPinCode()
                    if (currentPinCode.isNullOrEmpty()) {
                        userFormMutable.postValue(Event(Pair(userForm.consumer, true)))
                    } else {
                        userRepository.saveUserLocal(userForSave)
                        userRepository.updatePreviosAuthUser()
                        userRepository.setLastLoginUser(userForSave)
                        userFormMutable.postValue(Event(Pair(userForm.consumer, false)))
                    }
                    progressPhoneSending.postValue(false)
                }
            }
        }
    }



    private fun registration() {
        viewModelScope.launch(dispatcherProvider.io){
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
                                    var confirmErrorCode = gson.fromJson(it.data.data, ConfirmFormError::class.java)
                                    confirmErrorCode?.let {
                                        it.code?.let {
                                            userMessage.postValue(Event(it))
                                        }

                                    }

                                    var confirmError = gson.fromJson(it.data.data, LoginFormError::class.java)
                                    confirmError.email?.let {
                                        userForm.email = ""
                                        userMessageWithoutButton.postValue(Event(confirmError.email[0]))
                                    }
                                 
                                    progressPhoneSending.postValue(false)
                                    clearSmsForm.postValue(Event(true))
                                }
                            }
                        }
                        is Result.Error -> {
                            clearSmsForm.postValue(Event(true))
                            userMessage.postValue(Event("Ошибка"))
                            progressPhoneSending.postValue(false)
                            Log.d("RegistrationViewMode", "Succ")
                        }
                    }
                }
        }
    }

    fun selectSentByEmail() {
        when (formType) {
            "Login" -> {
                loginByEmail.postValue(Event(true))
            }
            "Registration" -> {
                if (userForm.email.length == 0) {
                    getOpenDialogEmail().postValue(Event(true))
                } else {
                    repeatSmsLoginByEmail()
                }
            }
        }
    }

    private fun repeatSmsLoginByEmail() {
        viewModelScope.launch {
            userRepository.sendCode(userForm.email)
                .onStart {
                    startTimerForEmail()
                }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            startTimerForEmail()
                            Log.d("ConfirmViewModel", "success")
                        }
                        is Result.Error -> {
                            Log.d("ConfirmViewModel", "error")

                        }
                        is Result.ErrorResponse -> {
                            var smsError = gson.fromJson(it.data.data, APIResponse::class.java)
                            Log.d("ConfirmViewModel", "error response "+smsError)
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

    fun getUserForm(): MutableLiveData<Event<Pair<UserForm, Boolean>>> {
        return userFormMutable
    }

    fun getCongratulation(): MutableLiveData<Event<String>> {
        return congratulation
    }

    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }

    fun getClearSmsForm(): MutableLiveData<Event<Boolean>> {
        return clearSmsForm
    }


    fun destoyTimer() {
        timerSms?.let {
            it.cancel()
        }
    }

    fun setUserForm(userForm: UserForm) {
        this.phone = userForm.phone
        this.userForm = userForm
        repeatSmsLogin()
        availableSendSms.postValue(Event(false))
        congratulation.postValue(Event(userForm.fio))

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

    fun getOpenDialogEmail(): MutableLiveData<Event<Boolean>> {
        return openDialogEmail
    }

    private fun startTimerForEmail() {

        timerEmail?.let {
            it.cancel()
            it.onFinish()
        }

        timerEmail = object: CountDownTimer(TIME_FOR_REPEAT_SMS_SEND.toLong(), 1000) {
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
                onlyEmail = true
                availableEmailSendSms.postValue(Event(true))
            }
        }
        timerEmail?.let {
            availableEmailSendSms.postValue(Event(false))

            it.start()
        }
    }

    fun setEmailFromDialog(email: String) {
         if (email.isEmailValid()) {
             userForm.email = email
             selectSentByEmail()
        } else {
           userMessageWithoutButton.postValue(Event("Вы неправильно указали адрес электронной почты!"))
        }
    }

    fun getLoginByEmail(): MutableLiveData<Event<Boolean>> {
        return loginByEmail
    }

    fun getSmsCode(): MutableLiveData<Event<String>> {
        return smsCodeMutable
    }

    private fun repeatSmsLogin() {
        viewModelScope.launch {
            userRepository.sendCode(phone)
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
                            onlyEmail = true
                            availableEmailSendSms.postValue(Event(true))
                        }
                    }
                }
        }
    }

    private fun repeatSmsRegistration() {
        viewModelScope.launch {
            userRepository.sendCode(phone)
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
                            onlyEmail = true
                            availableEmailSendSms.postValue(Event(true))
                        }
                    }
                }
        }
    }

    fun repeatSendSms() {

        if (onlyEmail) {
            if (userForm.email.length == 0) {
                getOpenDialogEmail().postValue(Event(true))
            } else {
                repeatSmsLoginByEmail()
            }
        } else {
            when (formType) {
                "Login" -> repeatSmsLogin()
                "Registration" -> repeatSmsRegistration()
            }
        }
    }

    fun getAvailableSendSms(): MutableLiveData<Event<Boolean>> {
        return availableSendSms
    }

    fun getUserMessageWithoutButton(): MutableLiveData<Event<String>> {
        return userMessageWithoutButton
    }

    fun getAvailableEmailSendSms(): MutableLiveData<Event<Boolean>> {
        return availableEmailSendSms
    }
}