package com.patstudio.communalka.presentation.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.model.auth.ConfirmSmsWrapper
import com.patstudio.communalka.data.model.auth.LoginFormError
import com.patstudio.communalka.data.repository.premises.PremisesRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import isEmailValid
import isValidPhoneNumber
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeViewModel(private val userRepository: UserRepository, private val premisesRepository: PremisesRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private var user: User? = null
    private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()
    private val navigateTo: MutableLiveData<Event<String>> = MutableLiveData()
    private val pinForm: MutableLiveData<Event<UserForm>> = MutableLiveData()

    private val placementListMutable: MutableLiveData<Event<List<Placement>>> = MutableLiveData()
    private var needEnterPin = true

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(Event(user))
   }

    private fun getUserPremises() {
        viewModelScope.launch(dispatcherProvider.io) {
            premisesRepository.getUserPremises()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    it?.let {
                        when (it) {
                            is Result.Success -> {
                                var placementList: PlacementWrapper = gson.fromJson(it.data.data, PlacementWrapper::class.java)
                                if (placementList.placements.count() > 0) {
                                    var placementLocal = premisesRepository.getUserPremises(user!!.id)
                                    Log.d("WelcomeViewModel", "local "+placementLocal)
                                    placementList.placements.map { parent ->
                                        placementLocal.map { child ->
                                            if (parent.id.compareTo(child.id) == 0) {
                                                Log.d("WelcomeViewModel", "find local")
                                                parent.imageType = child.imageType
                                                parent.path = child.imagePath
                                            }
                                        }
                                    }
                                    placementListMutable.postValue(Event(placementList.placements))
                                }
                            }
                            is Result.ErrorResponse -> { }
                            is Result.Error -> { }
                        }
                    }
                }
        }
    }

    fun setNeedEnterPin(needPin: Boolean) {
        Log.d("WelcomeViewModel", "need pin: "+needPin)
        this.needEnterPin = needPin
    }

    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.getLastAuthUser()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                   it?.let {
                       user = it
                       if (needEnterPin) {
                           var userForm = UserForm(it.id, it.name, it.phone, it.email, "AUTH", it.token, it.refresh)
                           pinForm.postValue(Event(userForm))
                       } else {
                           Log.d("WelcomeViewModel", it.toString())
                           userMutable.postValue(Event(it))
                           getUserPremises()
                       }

                   }
                }
        }
    }



    fun checkAvailableToOpenAddRoom() {
        if (user != null) {
           navigateTo.postValue(Event("ADD_ROOM"))
        } else {
            navigateTo.postValue(Event("REGISTRATION"))
        }
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }

    fun getPinForm(): MutableLiveData<Event<UserForm>> {
        return pinForm
    }

    fun getPlacementList(): MutableLiveData<Event<List<Placement>>> {
        return placementListMutable
    }

    fun getNavigateTo(): MutableLiveData<Event<String>> {
        return navigateTo
    }

}