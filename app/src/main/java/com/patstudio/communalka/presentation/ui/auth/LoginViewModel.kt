package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.ConfirmSmsParams
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.repository.user.UserRepository
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val userRepository: UserRepository): ViewModel() {

    private var phoneNumber: String = ""

    private val phoneError: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val disableNavigation: MutableLiveData<Boolean> = MutableLiveData()
    private val confirmCode: MutableLiveData<String> = MutableLiveData()
    private val confirmSmsParams: MutableLiveData<Event<ConfirmSmsParams>> = MutableLiveData()
    private val userMessage: MutableLiveData<String> = MutableLiveData()

    private fun validatePhoneNumber(): Boolean {
        return if (phoneNumber.isValidPhoneNumber()) {
            true
        } else {
            phoneError.postValue(true)
            false
        }
    }

    fun login() {
       if (validatePhoneNumber()) {

           viewModelScope.launch {
                userRepository.login(phoneNumber)
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
                               confirmSmsParams.postValue(Event(ConfirmSmsParams(phoneNumber)))
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                            //   confirmSmsParams.postValue(null)
                           }
                           is Result.Error -> {
                               confirmSmsParams.postValue(Event(ConfirmSmsParams(phoneNumber)))
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                            //   confirmSmsParams.postValue(null)
//                               progressPhoneSending.postValue(false)
//                               disableNavigation.postValue(false)
//                               userMessage.postValue("Превышен лимит смс в сутки")
                           }
                       }

                   }
           }

       }
    }

    fun setPhoneNumber(phoneNumber: String) {
        Log.d("LoginViewModel", phoneNumber)
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

    fun getUserMessage(): MutableLiveData<String> {
        return userMessage
    }

    fun getProgressPhoneSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

}