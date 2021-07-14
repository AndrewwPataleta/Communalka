package com.patstudio.communalka.presentation.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeViewModel(private val userRepository: UserRepository): ViewModel() {

   private lateinit var user: User
   private val userMutable: MutableLiveData<User> = MutableLiveData()

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(user)

   }

    fun getUser(): MutableLiveData<User> {
        return userMutable
    }

}