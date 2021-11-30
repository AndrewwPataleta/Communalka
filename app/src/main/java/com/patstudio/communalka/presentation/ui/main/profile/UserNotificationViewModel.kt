package com.patstudio.communalka.presentation.ui.main.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Gcm
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.Result
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

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(Event(user))
   }

    fun initCurrentUser() {
        _showProgress.postValue(Event(true))
        viewModelScope.launch(dispatcherProvider.io) {
            val user = userRepository.getLastAuthUser()
            if (user != null)  {
                userMutable.postValue(Event(user))
                _showProgress.postValue(Event(false))
            }
        }
    }

    fun changePushEnable(enable: Boolean) {
        viewModelScope.launch(dispatcherProvider.io) {
            var gcm = Gcm(
                registration_id = userRepository.getCurrentFbToken(),
                application_id = BuildConfig.APPLICATION_ID,
                active = enable
            )
            userRepository.updateGcm(gcm)
                .onStart { _showProgress.postValue(Event(true)) }
                .catch { }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            user.notificationEnable = enable
                            userRepository.saveUserLocal(user)
                            _showProgress.postValue(Event(false))
                        }
                    }
                }
        }
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }



}