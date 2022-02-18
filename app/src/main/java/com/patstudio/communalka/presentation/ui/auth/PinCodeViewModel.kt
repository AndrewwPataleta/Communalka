package com.patstudio.communalka.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.*
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class PinCodeViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private var pinCode: String = ""
    private var pinCodeRepeat: String = ""
    private lateinit var userForm: UserForm
    private val pinCodeMutable: MutableLiveData<String> = MutableLiveData()
    private val pinCodeMode: MutableLiveData<Event<String>> = MutableLiveData()
    private val availableFingerPrint: MutableLiveData<Boolean> = MutableLiveData()
    private val alertMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val accessBack: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val user: MutableLiveData<Event<User>> = MutableLiveData()
    private var enterMode = "INSTALL"
    private lateinit var savedUser: User
    private var currentPinCode: String = ""

    private fun checkForInstall(symbol: String) {
        pinCode += symbol
        pinCodeMutable.postValue(pinCode)

        if (pinCode.length == 4) {
            enterMode = "REPEAT"
            pinCodeMode.postValue(Event(enterMode))
            pinCodeMutable.postValue(pinCodeRepeat)
        }

    }

    private fun clearPinForm() {
        pinCode = ""
        pinCodeRepeat = ""
        pinCodeMutable.postValue(pinCode)
    }

    private fun checkForRepeat(symbol: String) {
        pinCodeRepeat += symbol
        pinCodeMutable.postValue(pinCodeRepeat)

        if (pinCodeRepeat.length == 4) {
            if (pinCodeRepeat.compareTo(pinCode) == 0) {
                var userForSave = User(userForm.id,userForm.fio, userForm.phone, userForm.email, pinCode, userForm.token, userForm.refresh, true,
                    "")
                userRepository.setPinCode(pinCodeRepeat)
                viewModelScope.launch(dispatcherProvider.io) {
                    try {
                        userRepository.saveUserLocal(userForSave)
                        userRepository.updatePreviosAuthUser()
                        userRepository.setLastLoginUser(userForSave)
                        clearPinForm()
                        alertMessage.postValue(Event("PIN-код  установлен"))
                        user.postValue(Event(userForSave))
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                pinCodeRepeat = ""
                alertMessage.postValue(Event("PIN-код введен неверно"))
                pinCodeMutable.postValue(pinCodeRepeat)
            }
        }

    }


    private fun checkForAuth(symbol: String) {
        pinCode += symbol
        pinCodeMutable.postValue(pinCode)

        if (pinCode.length == 4 ) {
            if (currentPinCode.compareTo(pinCode) == 0) {
                viewModelScope.launch(dispatcherProvider.io) {
                    try {
                        userRepository.updatePreviosAuthUser()
                        userRepository.setLastLoginUser(savedUser)
                        clearPinForm()
                        user.postValue(Event(savedUser))
                    }catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                alertMessage.postValue(Event("PIN-код введен неверно"))
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

    fun getAccessBack(): MutableLiveData<Event<Boolean>> {
        return accessBack;
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
         currentPinCode = userRepository.getCurrentPinCode()

         Log.d("PinCodeViewModel", "current pin code ${currentPinCode}")

         if (currentPinCode.length == 0) {
             enterMode = "INSTALL"
             pinCodeMode.postValue((Event(enterMode)))
         } else {
             enterMode = "AUTH"
             availableFingerPrint.postValue(true)
             accessBack.postValue(Event(false))
             viewModelScope.launch(dispatcherProvider.io) {
                 savedUser = userRepository.getUserById(userForm.id)
                 if (savedUser.fingerPrintSignIn) {

                     availableFingerPrint.postValue(true)
                 } else {
                     availableFingerPrint.postValue(false)
                 }
                 pinCodeMode.postValue((Event(enterMode)))
             }
         }

//         when (enterMode.length) {
//              -> {
//
//
//             }
//             "REPEAT" -> {
//                 availableFingerPrint.postValue(false)
//                 pinCodeMode.postValue((Event(enterMode)))
//             }
//             "AUTH" -> {
//                 Log.d("PinCodeViewMode", "set user form")
//
//             }
//         }
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