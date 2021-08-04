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
    private val pinCodeMode: MutableLiveData<Event<String>> = MutableLiveData()
    private val availableFingerPrint: MutableLiveData<Boolean> = MutableLiveData()
    private val alertMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val user: MutableLiveData<Event<User>> = MutableLiveData()
    private var enterMode = "INSTALL"
    private lateinit var savedUser: User

    private fun checkForInstall(symbol: String) {
        pinCode += symbol
        pinCodeMutable.postValue(pinCode)

        if (pinCode.length == 4) {
            enterMode = "REPEAT"
            pinCodeMode.postValue(Event(enterMode))
            pinCodeMutable.postValue(pinCodeRepeat)
        }

    }

    private fun checkForRepeat(symbol: String) {
        pinCodeRepeat += symbol
        pinCodeMutable.postValue(pinCodeRepeat)

        if (pinCodeRepeat.length == 4) {
            if (pinCodeRepeat.compareTo(pinCode) == 0) {
                var userForSave = User(userForm.id,userForm.fio, userForm.phone, userForm.email, pinCode, userForm.token, userForm.refresh, true)
                viewModelScope.launch(dispatcherProvider.io) {
                    try {
                        userRepository.saveUser(userForSave)
                        userRepository.updatePreviosAuthUser()
                        userRepository.setLastLoginUser(userForSave)
                        user.postValue(Event(userForSave))
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                pinCodeRepeat = ""
                alertMessage.postValue(Event("Неверный пин-код"))
                pinCodeMutable.postValue(pinCodeRepeat)
            }
        }

    }


    private fun checkForAuth(symbol: String) {
        pinCode += symbol
        pinCodeMutable.postValue(pinCode)

        if (pinCode.length == 4 ) {
            if (savedUser.pinCode.compareTo(pinCode) == 0) {
                viewModelScope.launch(dispatcherProvider.io) {
                    try {

                        userRepository.updatePreviosAuthUser()
                        userRepository.setLastLoginUser(savedUser)
                        user.postValue(Event(savedUser))
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            } else {
                alertMessage.postValue(Event("Неверный пин-код"))
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

    fun getUser(): MutableLiveData<Event<User>> {
        return user
    }

    fun getAlertMessage(): MutableLiveData<Event<String>> {
        return alertMessage
    }

    fun getPinCodeMode(): MutableLiveData<Event<String>> {
        return pinCodeMode
    }

    fun fingerPrintSuccess() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.updatePreviosAuthUser()
            userRepository.setLastLoginUser(savedUser)
            user.postValue(Event(savedUser))
        }
    }

    fun fingerPrintError() {

    }

    private fun removeWhenInstallPinCode() {
        if (pinCode.length < 4 && pinCode.length > 0) {
            pinCode = ""
            pinCodeMutable.postValue(pinCode)
        }
    }

    private fun removeWhenRepeatPinCode() {
        if (pinCodeRepeat.length < 4 && pinCodeRepeat.length > 0) {
            pinCodeRepeat = ""
            pinCodeMutable.postValue(pinCodeRepeat)
        }
    }

    private fun removeWhenAuthPinCode() {
        if (pinCode.length > 0)
            pinCode = ""
        pinCodeMutable.postValue(pinCode)
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
        pinCodeMode.postValue((Event(enterMode)))
    }

     fun setUserForm(userForm: UserForm) {
         this.userForm = userForm
         enterMode = userForm.type
         when (enterMode) {
             "INSTALL" ->  {
                 availableFingerPrint.postValue(false)
                 pinCodeMode.postValue((Event(enterMode)))
             }
             "REPEAT" -> {
                 availableFingerPrint.postValue(false)
                 pinCodeMode.postValue((Event(enterMode)))
             }
             "AUTH" -> {
                 availableFingerPrint.postValue(true)
                 viewModelScope.launch(dispatcherProvider.io) {
                     userRepository.getUserById(userForm.id)
                         .catch {
                             enterMode = "INSTALL"
                             availableFingerPrint.postValue(false)
                             pinCodeMode.postValue((Event(enterMode)))
                         }
                         .collect {
                             Log.d("PinCodeViewModel", it.toString())
                             if (it.pinCode.isNotEmpty()) {
                                 savedUser = it
                                 availableFingerPrint.postValue(true)
                                 pinCodeMode.postValue((Event(enterMode)))
                             } else {
                                 enterMode = "INSTALL"
                                 availableFingerPrint.postValue(false)
                                 pinCodeMode.postValue((Event(enterMode)))
                             }
                         }
                 }
             }
         }
    }

    fun removeAllPin() {
        when (enterMode) {
            "INSTALL" -> removeWhenInstallPinCode()
            "REPEAT" -> removeWhenRepeatPinCode()
            "AUTH" -> removeWhenAuthPinCode()
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