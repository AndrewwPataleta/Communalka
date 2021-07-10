package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel(private val userRepository: UserRepository): ViewModel() {

    private var phoneNumber: String = ""
    private var userFio: String = ""
    private var userEmail: String = ""

    private val phoneError: MutableLiveData<Boolean> = MutableLiveData()
    private val userFioError: MutableLiveData<Boolean> = MutableLiveData()
    private val userEmailError: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val disableNavigation: MutableLiveData<Boolean> = MutableLiveData()
    private val smsCode: MutableLiveData<String> = MutableLiveData()

    private fun validateUserForm(): Boolean {
        var valid = true
         if (!phoneNumber.isValidPhoneNumber()) {
             Log.d("RegistrationViewModel", "not valid phone number")
            valid = false
        } else if (!userEmail.isEmailValid()) {
             Log.d("RegistrationViewModel", "not valid email")
            valid = false
        } else if (userFio.isNullOrBlank()) {
             Log.d("RegistrationViewModel", "not valid fio")
             valid = false
         }
        return valid
    }

    fun registration() {
       if (validateUserForm()) {
            smsCode.postValue("4312")
//           viewModelScope.launch {
//                userRepository.login(phoneNumber)
//                   .onStart {
//                       progressPhoneSending.postValue(true)
//                       disableNavigation.postValue(true)
//                   }
//                   .catch {
//                   }
//                   .collect {
//                       progressPhoneSending.postValue(false)
//                       disableNavigation.postValue(false)
//                   }
//           }

       }
    }

    fun setPhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun setUserFio(userFio: String) {
        this.userFio = userFio
    }

    fun setUserEmail(userEmail: String) {
        this.userEmail = userEmail
    }

    fun getPhoneError(): MutableLiveData<Boolean> {
        return phoneError
    }

    fun getUserEmailError(): MutableLiveData<Boolean> {
        return userEmailError
    }

    fun getUserFioError(): MutableLiveData<Boolean> {
        return userFioError
    }

    fun getSmsCode(): MutableLiveData<String> {
        return smsCode
    }

    fun getDisableNavigation(): MutableLiveData<Boolean> {
        return disableNavigation
    }

    fun getProgressPhoneSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

}