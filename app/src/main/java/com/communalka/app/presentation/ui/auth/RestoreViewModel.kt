package com.communalka.app.presentation.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.communalka.app.common.utils.Event
import com.communalka.app.data.model.ConfirmSmsParams
import com.communalka.app.data.model.Result
import com.communalka.app.data.model.auth.LoginResponseError
import com.communalka.app.data.repository.user.UserRepository
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RestoreViewModel(private val userRepository: UserRepository, private val gson: Gson): ViewModel() {

    private var phoneNumber: String = ""

    private val phoneError: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val disableNavigation: MutableLiveData<Boolean> = MutableLiveData()
    private val confirmCode: MutableLiveData<String> = MutableLiveData()
    private val confirmSmsParams: MutableLiveData<Event<ConfirmSmsParams>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()

    private fun validatePhoneNumber(): Boolean {
        return if (phoneNumber.isValidPhoneNumber()) {
            true
        } else {
            phoneError.postValue(true)
            false
        }
    }

    fun restore() {
           viewModelScope.launch {
                userRepository.login(phoneNumber)
                   .onStart {
                       progressPhoneSending.postValue(true)
                       disableNavigation.postValue(true)
                   }
                   .collect {
                       when (it) {
                           is Result.Success -> {
                               when(it.data.status) {
                                   "success" -> {
                                       confirmSmsParams.postValue(Event(ConfirmSmsParams(phoneNumber, true)))
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

    fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun getPhoneError(): MutableLiveData<Boolean> {
        return phoneError
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

    fun getProgressPhoneSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

}