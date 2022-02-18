package com.patstudio.communalka.presentation.ui.main.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.launch

class PersonalInfoViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private lateinit var user: User
    private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()
    private val checkReadExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val openExternalPermission: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val imageURI: MutableLiveData<Event<Uri>> = MutableLiveData()
    private var currentPath: String = ""
    private var userFio: String = ""
    private val userFioError: MutableLiveData<Event<String>> = MutableLiveData()
    private val userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    private var _openPermissionSettings: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val openPermissionSettings: LiveData<Event<Boolean>> = _openPermissionSettings

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

    fun haveReadExternalPermission(havePermission: Boolean) {
        if (havePermission) {
            openExternalPermission.postValue(Event(true))
        } else {
           _openPermissionSettings.postValue(Event(true))
        }
    }

    fun setUserAvatar(currentPath: Uri) {
        Log.d("PersonalInfo", currentPath.path!!+" "+currentPath+" "+currentPath.toString())
        this.currentPath = currentPath.toString()
        imageURI.postValue(Event(currentPath))
    }

    fun changeUserAvatar() {
        checkReadExternalPermission.postValue(Event(true))
    }

    private fun validateUserForm(): Boolean {
        var valid = true

        if (userFio.length == 0) {
            userFioError.postValue(Event("Вы не заполнили обязательное поле - ФИО"))
            valid = false
        } else if (userFio.trim().split(" ").size < 2) {
            userFioError.postValue(Event("Вы не заполнили обязательное поле - ФИО"))
            valid = false
        }

        return valid
    }

     fun editUser() {
        if (validateUserForm()) {
            user.name = userFio
            if (currentPath.length > 0) user.photoPath = currentPath
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.saveUserLocalWithRemote(user)
                userMessage.postValue(Event("Изменения сохранены"))
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