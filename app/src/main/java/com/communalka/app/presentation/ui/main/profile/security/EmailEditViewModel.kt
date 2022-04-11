package com.communalka.app.presentation.ui.main.profile.security

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.communalka.app.common.contextprovider.DispatcherProvider
import com.communalka.app.common.utils.Event
import com.communalka.app.data.model.Result
import com.communalka.app.data.model.User
import com.communalka.app.data.repository.user.UserRepository
import isEmailValid
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EmailEditViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private lateinit var user: User
    private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()
    private var userEmail: String = ""
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private var _openPermissionSettings: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val openPermissionSettings: LiveData<Event<Boolean>> = _openPermissionSettings

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    private var _finish: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val finish: LiveData<Event<Boolean>> = _finish

    private var _userEmailError: MutableLiveData<Event<String>> = MutableLiveData()
    val userEmailError: LiveData<Event<String>> = _userEmailError

    private var _openConfirmCode: MutableLiveData<Event<String>> = MutableLiveData()
    val openConfirmCode: LiveData<Event<String>> = _openConfirmCode

    fun setCurrentUser(user:User) {
        this.user = user
        userMutable.postValue(Event(user))
    }

    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()
            userMutable.postValue(Event(user))
        }
    }

    private fun validateUserForm(): Boolean {
        var valid = true

        if (!userEmail.isEmailValid() || userEmail.isEmpty()) {
            _userEmailError.postValue(Event("Вы неправильно указали адрес электронной почты!"))
            valid = false
        }

        return valid
    }

    fun editUser() {
        if (validateUserForm()) {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.updateEmailProfile(userEmail)
                    .collect {
                        when (it) {
                            is Result.Success -> {

                                _openConfirmCode.postValue(Event(userEmail))
//                                userRepository.updateEmail(userEmail, user.id)
//                                userMessage.postValue(Event("Изменения сохранены"))
//                                _finish.postValue(Event(true))
                            }
                            is Result.Error -> {

                            }
                            is Result.ErrorResponse -> {
                                when(it.data.status) {
                                    "error" -> {
                                        it.data.message?.let {
                                            userMessage.postValue(Event(it))
                                        }
                                    }
                                }
                            }
                        }
                    }


            }
        }
    }

    fun setUserEmail(userEmail: String) {
        this.userEmail = userEmail
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }


    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }

}