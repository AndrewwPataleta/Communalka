package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.*
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.repository.user.UserRepository
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PinCodeViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private var pinCode: String = ""
    private var pinCodeRepeat: String = ""
    private lateinit var userForm: UserForm
    private val pinCodeMutable: MutableLiveData<String> = MutableLiveData()
    private val pinCodeMode: MutableLiveData<String> = MutableLiveData()
    private val availableFingerPrint: MutableLiveData<Boolean> = MutableLiveData()
    private val alertMessage: MutableLiveData<String> = MutableLiveData()
    private val user: MutableLiveData<User> = MutableLiveData()
    private var enterMode = "INSTALL"
    private lateinit var savedUser: User

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
                var userForSave = User(userForm.id,userForm.fio, userForm.phone, userForm.email, pinCode, userForm.token)
                viewModelScope.launch(dispatcherProvider.io) {
                    try {
                        userRepository.saveUser(userForSave)
                        user.postValue(userForSave)
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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

        if (pinCode.length == 4 ) {
            if (savedUser.pinCode.compareTo(pinCode) == 0)
                user.postValue(savedUser)
            else {
                alertMessage.postValue("Неверный пин-код")
                pinCode = ""
                pinCodeMutable.postValue(pinCode)
            }
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
        user.postValue(savedUser)
    }

    fun fingerPrintError() {

    }

    private fun removeLastWhenInstallPinCode() {
        if (pinCode.length < 4 && pinCode.length > 0) {
            pinCode = pinCode.subSequence(0, pinCode.length-1).toString()
            pinCodeMutable.postValue(pinCode)
        }
    }

    private fun removeLastWhenRepeatPinCode() {
        if (pinCodeRepeat.length < 4 && pinCodeRepeat.length > 0) {
            pinCodeRepeat = pinCodeRepeat.subSequence(0, pinCodeRepeat.length-1).toString()
            pinCodeMutable.postValue(pinCodeRepeat)
        }
    }

    private fun removeLastWhenAuthPinCode() {
        if (pinCode.length > 0)
            pinCode = pinCode.subSequence(0, pinCode.length-1).toString()
        pinCodeMutable.postValue(pinCode)
    }

    private fun checkEnterMode() {
        when(enterMode) {
            "INSTALL" -> availableFingerPrint.postValue(false)
            "REPEAT" -> availableFingerPrint.postValue(false)
            "AUTH" -> availableFingerPrint.postValue(true)
        }
        pinCodeMode.postValue(enterMode)
    }

     fun setUserForm(userForm: UserForm) {
         this.userForm = userForm
         enterMode = userForm.type
         when (enterMode) {
             "INSTALL" ->  {
                 availableFingerPrint.postValue(false)
                 pinCodeMode.postValue(enterMode)
             }
             "REPEAT" -> {
                 availableFingerPrint.postValue(false)
                 pinCodeMode.postValue(enterMode)
             }
             "AUTH" -> {
                 availableFingerPrint.postValue(true)
                 viewModelScope.launch(dispatcherProvider.io) {
                     userRepository.getUserById(userForm.id)
                         .catch {
                             enterMode = "INSTALL"
                             availableFingerPrint.postValue(false)
                             pinCodeMode.postValue(enterMode)
                         }
                         .collect {
                             Log.d("PinCodeViewModel", "user "+it.toString())
                             savedUser = it
                             pinCodeMode.postValue(enterMode)
                         }
                 }
             }
         }
    }

    fun removeLastItem() {
        when (enterMode) {
            "INSTALL" -> removeLastWhenInstallPinCode()
            "REPEAT" -> removeLastWhenRepeatPinCode()
            "AUTH" -> removeLastWhenAuthPinCode()
        }

    }

}