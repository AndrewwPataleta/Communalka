package com.patstudio.communalka.presentation.ui.main.profile.security

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.Result
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EmailEditViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private lateinit var user: User
    private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()
    private var userEmail: String = ""
    private val userEmailError: MutableLiveData<Event<String>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()

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

        if (!userEmail.isEmailValid()) {
            userEmailError.postValue(Event("Вы неправильно указали адрес электронной почты!"))
            valid = false
        }
        return valid
    }

     fun editUser() {
        if (validateUserForm()) {
            user.email = userEmail
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.updateEmail(userEmail)
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                userRepository.saveUserLocal(user)
                                userMessage.postValue(Event("Изменения сохранены"))
                            }
                            is Result.Error -> {

                            }
                            is Result.ErrorResponse -> {

                            }
                        }
                    }


            }

        }
    }

    fun setUserEmail(setUserEmail: String) {
        this.userEmail = setUserEmail
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }


    fun getEmailError(): MutableLiveData<Event<String>> {
        return userEmailError
    }

    fun getUserMessage(): MutableLiveData<Event<String>> {
        return userMessage
    }

}