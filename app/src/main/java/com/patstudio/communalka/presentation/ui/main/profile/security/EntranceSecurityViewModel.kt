package com.patstudio.communalka.presentation.ui.main.profile.security

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(Event(user))

   }

    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
             user = userRepository.getLastAuthUser()
            if (user != null)  {
                userMutable.postValue(Event(user))
            }
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