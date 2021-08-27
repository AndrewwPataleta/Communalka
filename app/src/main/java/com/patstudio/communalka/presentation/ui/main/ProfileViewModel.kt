package com.patstudio.communalka.presentation.ui.main

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

class ProfileViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

   private lateinit var user: User
   private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()

   private val _haveNoAuthUser: MutableLiveData<Event<Boolean>> = MutableLiveData()
   private val showSwitchUserDialog: MutableLiveData<Event<List<User>>> = MutableLiveData()

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(Event(user))

   }

    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
            val user = userRepository.getLastAuthUser()
            if (user != null)  {
                Log.d("WelcomeViewModel", user.toString())
                userMutable.postValue(Event(user))
            } else {
                _haveNoAuthUser.postValue(Event(true))
            }
        }
    }

    fun logout() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.logoutAll()
            _haveNoAuthUser.postValue(Event(true))
        }
    }

    fun selectChangeProfile() {
        viewModelScope.launch(dispatcherProvider.io) {
            val users = userRepository.getUsers()
            showSwitchUserDialog.postValue(Event(users))
        }
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }

    fun getShowSwitchUsers(): MutableLiveData<Event<List<User>>> {
        return showSwitchUserDialog
    }

    fun getHaveNoAuth(): MutableLiveData<Event<Boolean>> {
        return _haveNoAuthUser
    }

}