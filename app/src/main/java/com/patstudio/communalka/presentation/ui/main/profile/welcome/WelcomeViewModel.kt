package com.patstudio.communalka.presentation.ui.main.profile.welcome

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WelcomeViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private var user: User? = null
    private val userMutable: MutableLiveData<Event<User>> = MutableLiveData()
    private val withoutUser: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val readStoragePermissionMutable: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val navigateTo: MutableLiveData<Event<String>> = MutableLiveData()
    private val pinForm: MutableLiveData<Event<UserForm>> = MutableLiveData()
    private val updateByPosition: MutableLiveData<Event<Int>> = MutableLiveData()
    private val editPlacementDialog: MutableLiveData<Event<Placement>> = MutableLiveData()
    private val editPlacement: MutableLiveData<Event<Placement>> = MutableLiveData()
    
    private val selectPersonalAccountPlacement: MutableLiveData<Event<Placement>> = MutableLiveData()

    private var _transmissionReadingPlacement: MutableLiveData<Event<Placement>> = MutableLiveData()
    val transmissionReadingPlacement: LiveData<Event<Placement>> = _transmissionReadingPlacement

    private var _loadingPlacement: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val loadingPlacement: LiveData<Event<Boolean>> = _loadingPlacement

    private val placementListMutable: MutableLiveData<Event<Pair<List<Placement>, Boolean>>> = MutableLiveData()
    private var needEnterPin: Boolean = true
    private var typeAuthChanged: Boolean = false

    private lateinit var userPlacement: List<Placement>

   fun setCurrentUser(user:User) {
       this.user = user
       userMutable.postValue(Event(user))
   }

    fun setReadStoragePermission(readStoragePermission: Boolean) {

            viewModelScope.launch(dispatcherProvider.io) {
                delay(1000)
                placementListMutable.postValue(Event(Pair(userPlacement, user!!.showPlacementTooltip)))
                updateUserTooltipSpawn()
            }

    }

    private fun updateUserTooltipSpawn() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.updateShowTooltip(user!!.id, false)
        }
    }

    private fun getUserPremises() {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getUserPremises()
                .onStart { _loadingPlacement.postValue(Event(true)) }
                .catch {
                    it.printStackTrace()
                }
                .collect {
                        when (it) {
                            is Result.Success -> {
                                var placementList: PlacementWrapper = gson.fromJson(it.data.data, PlacementWrapper::class.java)
                                if (placementList.placements.count() > 0) {
                                    var placementLocal = roomRepository.getUserPremises(user!!.id)
                                    placementList.placements.map { parent ->
                                        placementLocal.map { child ->
                                            if (parent.id.compareTo(child.id) == 0) {
                                                parent.imageType = child.imageType
                                                parent.path = child.imagePath
                                            }
                                        }
                                    }
                                    userPlacement = placementList.placements
                                    readStoragePermissionMutable.postValue(Event(true))
                                } else {
                                    userMutable.postValue(Event(user!!))
                                }
                            }
                            is Result.ErrorResponse -> { }
                            is Result.Error -> { }
                        }
                }
        }
    }

    fun setNeedEnterPin(needPin: Boolean) {
        Log.d("WelcomeViewModel", "need pin: "+needPin)
        this.typeAuthChanged = true
        this.needEnterPin = needPin
    }

    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
            val it = userRepository.getLastAuthUser()
            user = it

            if (user != null) {
                Log.d("WelcomeViewModel", "need pin before" +needEnterPin+" user auto "+!user!!.autoSignIn)

                needEnterPin = !user!!.autoSignIn

                Log.d("WelcomeViewModel", "need pin after "+needEnterPin+" type auth chnafd "+typeAuthChanged)

                if (needEnterPin && !typeAuthChanged) {

                    var userForm = UserForm(
                        it.id,
                        it.name,
                        it.phone,
                        it.email,
                        "AUTH",
                        it.token,
                        it.refresh,
                        fingerPrintSignIn = it.fingerPrintSignIn,
                        autoSignIn = it.autoSignIn
                    )
                    pinForm.postValue(Event(userForm))
                } else {

                    getUserPremises()
                }
            } else {
                withoutUser.postValue(Event(true))
            }
//            if (user != null) {
//                else {
//                    Log.d("WelcomeViewModel", it.toString())
//                    userMutable.postValue(Event(it))
//                    getUserPremises()
//                }
//            } else {
//
//            }

        }
    }

    fun selectTransmissionReading(placement: Placement) {
        _transmissionReadingPlacement.postValue(Event(placement))
    }
    
    fun checkAvailableToOpenAddRoom() {
        if (user != null) {
           navigateTo.postValue(Event("ADD_ROOM"))
        } else {
            navigateTo.postValue(Event("ADD_ROOM"))
        }
    }

    fun selectEdit(model: Placement) {
        editPlacementDialog.postValue(Event(model))
    }

    fun getUser(): MutableLiveData<Event<User>> {
        return userMutable
    }

    fun getPinForm(): MutableLiveData<Event<UserForm>> {
        return pinForm
    }

    fun getPlacementList(): MutableLiveData<Event<Pair<List<Placement>, Boolean>>> {
        return placementListMutable
    }

    fun getReadStoragePermission(): MutableLiveData<Event<Boolean>> {
        return readStoragePermissionMutable
    }

    fun getWithoutUser(): MutableLiveData<Event<Boolean>> {
        return withoutUser
    }

    fun getEditPlacementDialog(): MutableLiveData<Event<Placement>> {
        return editPlacementDialog
    }

    fun getEditPlacement(): MutableLiveData<Event<Placement>> {
        return editPlacement
    }


    fun selectEditPlacement(placement: Placement) {
        editPlacement.postValue(Event(placement))
    }

    fun selectPlacementForPersonalAccounts(placement: Placement) {
        selectPersonalAccountPlacement.postValue(Event(placement))
    }


    fun clickArrow(position: Int) {
        userPlacement[position].isOpened = !userPlacement[position].isOpened
        updateByPosition.postValue(Event(position))
    }

    fun getNavigateTo(): MutableLiveData<Event<String>> {
        return navigateTo
    }

    fun getPersonalAccountPlacement(): MutableLiveData<Event<Placement>> {
        return selectPersonalAccountPlacement
    }

    fun getUpdatePosition(): MutableLiveData<Event<Int>> {
        return updateByPosition
    }

}