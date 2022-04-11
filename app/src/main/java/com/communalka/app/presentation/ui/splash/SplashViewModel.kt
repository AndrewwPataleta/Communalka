package com.communalka.app.presentation.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.communalka.app.data.model.Result
import com.communalka.app.data.model.User
import com.communalka.app.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow

class SplashViewModel (private val userRepository: UserRepository): ViewModel() {

    private val query = MutableStateFlow("")

    val users: LiveData<Result<List<User>>> = userRepository.users.asLiveData()


}