package com.communalka.app.presentation.ui.main.profile.security

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.communalka.app.common.utils.Event
import com.communalka.app.data.model.User
import com.communalka.app.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class EntranceSecurityViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private lateinit var user: User
    private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()
    private val checkReadExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val openExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val imageURI: MutableLiveData<Event<Uri>> = MutableLiveData()
    private var currentPath: String = ""
    private var userFio: String = ""
    private val userFioError: MutableLiveData<Event<String>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()


    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
             user = userRepository.getLastAuthUser()
            if (user != null)  {
                userMutable.postValue(Event(user))
            }
        }
    }

    fun setAutoSignIn(autoSignIn: Boolean) {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.updateAuthSignIn(autoSignIn, user.id)
        }
    }

    fun setFingerPrintAvailable(fingerPrintAvailable: Boolean) {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.updateFingerPrintAvailable(fingerPrintAvailable, user.id)
        }

    }

    fun setUserFio(userFio: String) {
        this.userFio = userFio
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }

    fun getImageURI() : MutableLiveData<Event<Uri>> {
        return imageURI
    }

    fun getCheckExternalPermission() : MutableLiveData<Event<Boolean>> {
        return checkReadExternalPermission
    }

    fun getOpenExternalPermission() : MutableLiveData<Event<Boolean>> {
        return openExternalPermission
    }


    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }

    fun getUserFioError(): MutableLiveData<Event<String>> {
        return userFioError
    }
}