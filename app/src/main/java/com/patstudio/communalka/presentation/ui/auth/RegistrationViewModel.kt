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

    private var userEmail: String = ""
    private var licenseAccept: Boolean = false
    private val phoneError: MutableLiveData<Event<String>> = MutableLiveData()
    private var userFio: String = ""
    private val userFioError: MutableLiveData<Event<String>> = MutableLiveData()
    private val userEmailError: MutableLiveData<Event<String>> = MutableLiveData()
    private val userLicenseAcceptError: MutableLiveData<Event<String>> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val disableNavigation: MutableLiveData<Boolean> = MutableLiveData()
    private val userForm: MutableLiveData<Event<UserForm>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()

    private fun validateUserForm(): Boolean {
        var valid = true
         if (phoneNumber.length != 12) {
             phoneError.postValue(Event("Вы неправильно указали номер телефона!"))
            valid = false
        }
        if (!licenseAccept) {
            userLicenseAcceptError.postValue(Event("Подтвердите согласие с офертой"))
            valid = false
        }
         if (userFio.length == 0) {
            userFioError.postValue(Event("Вы не заполнили обязательное поле - ФИО. Заполните, и продолжите регистрацию"))
            valid = false
         } else if (userFio.trim().split(" ").size < 2) {
             userFioError.postValue(Event("Вы не заполнили обязательное поле - ФИО. Заполните, и продолжите регистрацию"))
             valid = false
         }
        if (userEmail.length > 0) {
            if (!userEmail.isEmailValid()) {
                userEmailError.postValue(Event("Вы неправильно указали адрес электронной почты!"))
                valid = false
            }
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
                                       var userFormResp = UserForm("",userFio,phoneNumber,userEmail,"INSTALL","","", fingerPrintSignIn = false, autoSignIn = false)
                                       userForm.postValue(Event(userFormResp))
                                       progressPhoneSending.postValue(false)
                                       disableNavigation.postValue(false)
                                   }
                               }
                           }
                           is Result.ErrorResponse -> {
                               when(it.data.status) {
                                   "fail" -> {
                                       var confirmError = gson.fromJson(it.data.data, LoginFormError::class.java)
                                       confirmError.email?.let {
                                           userEmailError.postValue(Event(confirmError.email[0]))
                                       }
                                       confirmError.fio?.let {
                                           userFioError.postValue(Event(confirmError.fio[0]))
                                       }
                                       confirmError.phone?.let {
                                           phoneError.postValue(Event(confirmError.phone[0]))
                                       }
                                   }
                               }
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                           }
                           is Result.Error -> {
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                               userMessage.postValue(Event("Проверьте корректность заполнения полей"))
                           }
                       }
                   }
           }
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

    fun getPhoneError(): MutableLiveData<Event<String>> {
        return phoneError
    }

    fun getUserEmailError(): MutableLiveData<Event<String>> {
        return userEmailError
    }

    fun getUserFioError(): MutableLiveData<Event<String>> {
        return userFioError
    }

    fun getLicenseError(): MutableLiveData<Event<String>> {
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

    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }

    fun getProgressPhoneSending(): MutableLiveData<Boolean> {
        return progressPhoneSending
    }

}