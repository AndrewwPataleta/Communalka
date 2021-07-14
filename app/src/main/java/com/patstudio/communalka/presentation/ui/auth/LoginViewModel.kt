package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.data.model.ConfirmSmsParams
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
    private val confirmSmsParams: MutableLiveData<ConfirmSmsParams> = MutableLiveData()

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
                        confirmSmsParams.postValue(ConfirmSmsParams(phoneNumber))
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

    fun getConfirmSmsParams(): MutableLiveData<ConfirmSmsParams> {
        return confirmSmsParams
    }

    fun getDisableNavigation(): MutableLiveData<Boolean> {
        return disableNavigation
    }

    fun getProgressPhoneSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

}