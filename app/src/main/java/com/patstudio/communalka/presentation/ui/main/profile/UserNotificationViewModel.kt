package com.patstudio.communalka.presentation.ui.main.profile

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

class UserNotificationViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

   private lateinit var user: User
   private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()



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
            }
        }
    }


    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }



}