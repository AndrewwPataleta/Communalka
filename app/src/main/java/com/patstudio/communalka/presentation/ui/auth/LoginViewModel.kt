package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.ConfirmSmsParams
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.auth.LoginFormError
import com.patstudio.communalka.data.model.auth.LoginResponseError
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository, private val gson: Gson): ViewModel() {

    private var phoneNumber: String = ""
    private var email: String = ""

    private val phoneError: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val emailError: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val disableNavigation: MutableLiveData<Boolean> = MutableLiveData()
    private val confirmCode: MutableLiveData<String> = MutableLiveData()
    private val confirmSmsParams: MutableLiveData<Event<ConfirmSmsParams>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val loginTypeMutable: MutableLiveData<Event<String>> = MutableLiveData()
    private var loginType = "phone"

    private fun validateForm(): Boolean {
        when (loginType) {
            "default" -> {

                Log.d("LoginViewModel", "phone number "+phoneNumber+" validate "+(phoneNumber.length == 12)+" "+phoneNumber.isEmailValid())
                return if (phoneNumber.length > 11 && phoneNumber.length <= 12 || phoneNumber.isEmailValid()) {
                    true
                } else {
                    phoneError.postValue(Event(true))
                    false
                }
            }
            "phone" -> {
                return if (phoneNumber.length == 12) {
                    true
                } else {
                    phoneError.postValue(Event(true))
                    false
                }
            }
            "email" -> {
                Log.d("LoginViewModel", "email validate "+email)
                return if (email.isEmailValid()) {
                    true
                } else {
                    emailError.postValue(Event(true))
                    false
                }
            }
        }
        return true
    }

    fun login() {
       if (validateForm()) {
           var value = ""
           when (loginType) {
               "default" -> {
                   value = phoneNumber
               }
               "phone" -> {
                 value = phoneNumber
               }
               "email" -> {
                 value = email
               }
           }
           viewModelScope.launch {
                userRepository.login(value)
                   .onStart {
                       progressPhoneSending.postValue(true)
                       disableNavigation.postValue(true)
                   }
                   .catch {
                       Log.d("LoginViewModel", it.localizedMessage)
                   }
                   .collect {
                       when (it) {
                           is Result.Success -> {
                               when(it.data.status) {
                                   "success" -> {
                                       userRepository.sendCode(value)
                                           .catch {
                                               Log.d("LoginViewModel", it.localizedMessage)
                                           }
                                           .collect {
                                               when (it) {
                                                   is Result.Success -> {
                                                       when(it.data.status) {
                                                           "fail" -> {
                                                               var loginError = gson.fromJson(it.data.data, LoginFormError::class.java)
                                                               Log.d("LoginViewModel", loginError.toString())
                                                           }
                                                           "success" -> {
                                                               confirmSmsParams.postValue(Event(ConfirmSmsParams(value,false)))
                                                               progressPhoneSending.postValue(false)
                                                               disableNavigation.postValue(false)
                                                           }
                                                       }
                                                   }
                                                   is Result.Error -> {
                                                       progressPhoneSending.postValue(false)
                                                       disableNavigation.postValue(false)
                                                   }
                                                   is Result.ErrorResponse -> {
                                                       when(it.data.status) {
                                                           "fail" -> {
                                                               it.data.message?.let {
                                                                   userMessage.postValue(Event(it))
                                                               }

                                                               it.data?.let {
                                                                   var loginResponseError = gson.fromJson(it.data, LoginResponseError::class.java)
                                                                   loginResponseError?.let {
                                                                       userMessage.postValue(Event(it.target))
                                                                   }
                                                               }
                                                               progressPhoneSending.postValue(false)
                                                               disableNavigation.postValue(false)
                                                           }
                                                       }
                                                   }
                                               }
                                           }
                                   }
                               }
                           }
                           is Result.Error -> {
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                           }
                           is Result.ErrorResponse -> {
                               when(it.data.status) {
                                   "fail" -> {
                                       it.data.message?.let {
                                           userMessage.postValue(Event(it))
                                       }

                                       it.data?.let {
                                           var loginResponseError = gson.fromJson(it.data, LoginResponseError::class.java)
                                           loginResponseError?.let {
                                               userMessage.postValue(Event(it.target))
                                           }
                                       }

                                       progressPhoneSending.postValue(false)
                                       disableNavigation.postValue(false)
                                   }
                               }
                           }
                       }
                   }
           }
       }
    }

    fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }



    fun setEmail(email: String) {
        this.email = email
    }

    fun getPhoneError(): MutableLiveData<Event<Boolean>> {
        return phoneError
    }

    fun getEmailError(): MutableLiveData<Event<Boolean>> {
        return emailError
    }

    fun setLoginType(type: String?) {
        if (type != null)
            this.loginType = type;
        loginTypeMutable.postValue(Event(loginType))
    }

    fun getConfirmCode(): MutableLiveData<String> {
        return confirmCode
    }

    fun getConfirmSmsParams(): MutableLiveData<Event<ConfirmSmsParams>> {
        return confirmSmsParams
    }

    fun getDisableNavigation(): MutableLiveData<Boolean> {
        return disableNavigation
    }

    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }

    fun getLoginType(): MutableLiveData<Event<String>> {
        return loginTypeMutable
    }

    fun getProgressPhoneSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

}