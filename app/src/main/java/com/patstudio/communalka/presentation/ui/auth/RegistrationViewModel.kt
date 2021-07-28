package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.model.auth.LoginFormError
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegistrationViewModel(private val userRepository: UserRepository, private val gson: Gson): ViewModel() {

    private var phoneNumber: String = ""
    private var userFio: String = ""
    private var userEmail: String = ""
    private var licenseAccept: Boolean = false
    private val phoneError: MutableLiveData<String> = MutableLiveData()
    private val userFioError: MutableLiveData<String> = MutableLiveData()
    private val userEmailError: MutableLiveData<String> = MutableLiveData()
    private val userLicenseAcceptError: MutableLiveData<String> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val disableNavigation: MutableLiveData<Boolean> = MutableLiveData()
    private val userForm: MutableLiveData<Event<UserForm>> = MutableLiveData()
    private val userMessage: MutableLiveData<String> = MutableLiveData()

    private fun validateUserForm(): Boolean {
        var valid = true
         if (!phoneNumber.isValidPhoneNumber()) {
             phoneError.postValue("Проверьте номер телефона")
            valid = false
        }
        if (!licenseAccept) {
            userLicenseAcceptError.postValue("Подтвердите согласие с офертой")
            valid = false
        }
         if (userFio.length == 0) {
            userFioError.postValue("Проверьте ваше ФИО")
            valid = false
         }
        return valid
    }

    fun registration() {
       if (validateUserForm()) {

           viewModelScope.launch {
               userRepository.registration(userFio, phoneNumber, userEmail)
                   .onStart {
                       progressPhoneSending.postValue(true)
                       disableNavigation.postValue(true)
                   }
                   .catch {

                   }
                   .collect {
                       when (it) {
                           is Result.Success -> {
                               when(it.data.status) {
                                   "success" -> {
                                       var userFormResp = UserForm("",userFio,phoneNumber,userEmail,"INSTALL","")
                                       userForm.postValue(Event(userFormResp))
                                       progressPhoneSending.postValue(false)
                                       disableNavigation.postValue(false)
                                   }
                               }
                           }
                           is Result.ErrorResponse -> {
                               Log.d("RegistrationViewModel", "Error Response "+ it.toString())
                               when(it.data.status) {
                                   "fail" -> {
                                       var confirmError = gson.fromJson(it.data.data, LoginFormError::class.java)
                                       Log.d("RegistrationViewModel", confirmError.toString())
                                       confirmError.email?.let {
                                           userEmailError.postValue(confirmError.email[0])
                                       }
                                       confirmError.fio?.let {
                                           userFioError.postValue(confirmError.fio[0])
                                       }
                                       confirmError.phone?.let {
                                           phoneError.postValue(confirmError.phone[0])
                                       }
                                   }
                               }
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                           }
                           is Result.Error -> {
                               Log.d("RegistrationViewModel", "Error "+it.toString())
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                               userMessage.postValue("Проверьте корректность заполнения полей")
                           }
                       }
                   }
           }


       }
//       else {
//           userMessage.postValue("Проверьте корректность заполнения полей")
//       }
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

    fun getPhoneError(): MutableLiveData<String> {
        return phoneError
    }

    fun getUserEmailError(): MutableLiveData<String> {
        return userEmailError
    }

    fun getUserFioError(): MutableLiveData<String> {
        return userFioError
    }

    fun getLicenseError(): MutableLiveData<String> {
        return userLicenseAcceptError
    }

    fun getUserForm(): MutableLiveData<Event<UserForm>> {
        return userForm
    }

    fun getDisableNavigation(): MutableLiveData<Boolean> {
        return disableNavigation
    }

    fun userLicenseAgreement(accept: Boolean) {
        licenseAccept = accept
    }

    fun getUserMessage(): MutableLiveData<String> {
        return userMessage
    }

    fun getProgressPhoneSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

}