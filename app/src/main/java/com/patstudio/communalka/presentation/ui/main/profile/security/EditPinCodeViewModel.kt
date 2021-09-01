package com.patstudio.communalka.presentation.ui.main.profile.security

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

class EditPinCodeViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private var pinCode: String = ""
    private var pinCodeRepeat: String = ""
    private lateinit var userForm: UserForm
    private val pinCodeMutable: MutableLiveData<String> = MutableLiveData()
    private val pinCodeMode: MutableLiveData<Event<String>> = MutableLiveData()
    private val availableFingerPrint: MutableLiveData<Boolean> = MutableLiveData()
    private val alertMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private val accessBack: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val openSecurityPage: MutableLiveData<Event<Boolean>> = MutableLiveData()
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

                viewModelScope.launch(dispatcherProvider.io) {
                    try {
                        userRepository.updatePinCode(savedUser.id, pinCodeRepeat)
                        clearPinForm()
                        alertMessage.postValue(Event("Новый PIN-код  установлен"))
                        openSecurityPage.postValue(Event(true))

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




    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
            val user = userRepository.getLastAuthUser()
            if (user != null)  {
                savedUser = user
            }
        }
    }


    fun clickDigital(symbol: String) {
        when (enterMode) {
            "INSTALL" -> checkForInstall(symbol)
            "REPEAT" -> checkForRepeat(symbol)
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

    fun getOpenSecurityPage(): MutableLiveData<Event<Boolean>> {
        return openSecurityPage
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



     fun init() {
         initCurrentUser()
         pinCodeMode.postValue((Event(enterMode)))
     }


    fun removeLastItem() {
        when (enterMode) {
            "INSTALL" -> removeLastWhenInstallPinCode()
            "REPEAT" -> removeLastWhenRepeatPinCode()
        }

    }

}