package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegistrationViewModel(private val userRepository: UserRepository): ViewModel() {

    private var phoneNumber: String = ""
    private var userFio: String = ""
    private var userEmail: String = ""
    private var licenseAccept: Boolean = false

    private val phoneError: MutableLiveData<Boolean> = MutableLiveData()
    private val userFioError: MutableLiveData<Boolean> = MutableLiveData()
    private val userEmailError: MutableLiveData<Boolean> = MutableLiveData()
    private val progressPhoneSending: MutableLiveData<Boolean> = MutableLiveData()
    private val disableNavigation: MutableLiveData<Boolean> = MutableLiveData()
    private val userForm: MutableLiveData<Event<UserForm>> = MutableLiveData()
    private val userMessage: MutableLiveData<String> = MutableLiveData()

    private fun validateUserForm(): Boolean {
        var valid = true
         if (!phoneNumber.isValidPhoneNumber()) {
             Log.d("RegistrationViewModel", "not valid phone number")
            valid = false
        }
        if (!licenseAccept) {
            Log.d("RegistrationViewModel", "not accept")
            valid = false
        }
         else if (!userEmail.isEmailValid()) {
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
                                userForm.postValue(Event(UserForm(userFio, phoneNumber, userEmail, "INSTALL")))
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                           }
                           is Result.Error -> {
                               progressPhoneSending.postValue(false)
                               disableNavigation.postValue(false)
                               userMessage.postValue("Проверьте корректность заполнения полей")
                           }
                       }
                   }
           }


       } else {
           userMessage.postValue("Проверьте корректность заполнения полей")
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