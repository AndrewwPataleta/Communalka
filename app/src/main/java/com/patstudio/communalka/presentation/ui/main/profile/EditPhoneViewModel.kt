 package com.patstudio.communalka.presentation.ui.main.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Item
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.model.auth.LoginResponseError
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import isValidPhoneNumber
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditPhoneViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private lateinit var user: User
    private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()
    private var userPhone: String = ""
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private var _openPermissionSettings: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val openPermissionSettings: LiveData<Event<Boolean>> = _openPermissionSettings

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    private var _finish: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val finish: LiveData<Event<Boolean>> = _finish

    private var _openConfirmCode: MutableLiveData<Event<String>> = MutableLiveData()
    val openConfirmCode: LiveData<Event<String>> = _openConfirmCode




    private var _userPhoneError: MutableLiveData<Event<String>> = MutableLiveData()
    val userPhoneError: LiveData<Event<String>> = _userPhoneError

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

    private fun validateUserForm(): Boolean {
        var valid = true

        if (userPhone.length != 12 ) {
            _userPhoneError.postValue(Event("Вы неправильно указали номер телефона!"))
            valid = false
        }

        return valid
    }

     fun editUser() {
        if (validateUserForm()) {
            viewModelScope.launch(dispatcherProvider.io) {
                _openConfirmCode.postValue(Event(userPhone))
            }
        }
    }

    fun setUserPhone(userPhone: String) {
        this.userPhone = userPhone
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }
    

    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }
    
}