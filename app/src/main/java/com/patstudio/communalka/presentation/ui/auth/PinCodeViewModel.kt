package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.repository.user.UserRepository
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PinCodeViewModel(private val userRepository: UserRepository): ViewModel() {

    private var pinCode: String = ""
    private var pinCodeRepeat: String = ""
    private lateinit var userForm: UserForm
    private val pinCodeMutable: MutableLiveData<String> = MutableLiveData()
    private val pinCodeMode: MutableLiveData<String> = MutableLiveData()
    private val availableFingerPrint: MutableLiveData<Boolean> = MutableLiveData()
    private val alertMessage: MutableLiveData<String> = MutableLiveData()
    private val user: MutableLiveData<User> = MutableLiveData()
    private var enterMode = "INSTALL"

    private fun checkForInstall(symbol: String) {
        pinCode += symbol
        pinCodeMutable.postValue(pinCode)

        if (pinCode.length == 4) {
            enterMode = "REPEAT"
            pinCodeMode.postValue(enterMode)
            pinCodeMutable.postValue(pinCodeRepeat)
        }

    }

    private fun checkForRepeat(symbol: String) {
        pinCodeRepeat += symbol
        pinCodeMutable.postValue(pinCodeRepeat)

        if (pinCodeRepeat.length == 4) {
            if (pinCodeRepeat.compareTo(pinCode) == 0) {
                user.postValue(User("1",userForm.fio, userForm.phone))
            } else {
                pinCodeRepeat = ""
                alertMessage.postValue("Неверный пин-код")
                pinCodeMutable.postValue(pinCodeRepeat)
            }
        }

    }


    private fun checkForAuth(symbol: String) {
        pinCode += symbol
        pinCodeMutable.postValue(pinCode)

        if (pinCode.length == 4) {
            user.postValue(User("1",userForm.fio, userForm.phone))
        }
    }

    fun clickDigital(symbol: String) {
        when (enterMode) {
            "INSTALL" -> checkForInstall(symbol)
            "REPEAT" -> checkForRepeat(symbol)
            "AUTH" -> checkForAuth(symbol)
        }

    }

    fun getPinCode(): MutableLiveData<String> {
        return pinCodeMutable
    }

    fun getAvailableFingerPrint(): MutableLiveData<Boolean> {
        return availableFingerPrint
    }

    fun getUser(): MutableLiveData<User> {
        return user
    }

    fun getAlertMessage(): MutableLiveData<String> {
        return alertMessage
    }

    fun getPinCodeMode(): MutableLiveData<String> {
        return pinCodeMode
    }

    fun fingerPrintSuccess() {
        user.postValue(User("1",userForm.fio, userForm.phone))
    }

    fun fingerPrintError() {

    }

    private fun removeLastWhenInstallPinCode() {
        if (pinCode.length < 4 && pinCode.length > 0) {
            pinCode = pinCode.subSequence(0, pinCode.length-1).toString()
        } else if (pinCodeRepeat.length < 4 && pinCodeRepeat.length > 0) {
            pinCodeRepeat = pinCodeRepeat.subSequence(0, pinCodeRepeat.length-1).toString()
        }
    }

    private fun removeLastWhenAuthPinCode() {
        if (pinCode.length > 0)
            pinCode = pinCode.subSequence(0, pinCode.length-1).toString()
        pinCodeMutable.postValue(pinCode)
    }

    fun setUserForm(userForm: UserForm) {
        this.userForm = userForm
        enterMode = userForm.type
        pinCodeMode.postValue(enterMode)
    }

    fun removeLastItem() {
        when (enterMode) {
            "INSTALL" -> removeLastWhenInstallPinCode()
            "AUTH" -> removeLastWhenAuthPinCode()
        }

    }

}