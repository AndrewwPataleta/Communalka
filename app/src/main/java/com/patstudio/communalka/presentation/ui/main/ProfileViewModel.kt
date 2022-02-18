package com.patstudio.communalka.presentation.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

   private lateinit var user: User
   private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()

   private val _haveNoAuthUser: MutableLiveData<Event<Boolean>> = MutableLiveData()
   private val showSwitchUserDialog: MutableLiveData<Event<List<User>>> = MutableLiveData()
   private val closeSwitchDialog: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private lateinit var users: List<User>

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(Event(user))

   }

    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
            val user = userRepository.getLastAuthUser()
            if (user != null)  {

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
            users = userRepository.getUsers()
            showSwitchUserDialog.postValue(Event(users))
        }
    }

    fun selectSwitchUserByPosition(position: Int) {
        val user = users[position]
        viewModelScope.launch(dispatcherProvider.io) {
            closeSwitchDialog.postValue(Event(true))
            userRepository.updatePreviosAuthUser()
            userRepository.setLastLoginUser(user)
            initCurrentUser()
        }
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }

    fun getShowSwitchUsers(): MutableLiveData<Event<List<User>>> {
        return showSwitchUserDialog
    }

    fun getCloseSwitchDialog(): MutableLiveData<Event<Boolean>> {
        return closeSwitchDialog
    }

    fun getHaveNoAuth(): MutableLiveData<Event<Boolean>> {
        return _haveNoAuthUser
    }

}