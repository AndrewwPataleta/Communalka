package com.patstudio.communalka.presentation.ui.main.readings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TransmissionReadingListViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentPersonalAccount: PersonalAccount
    private lateinit var currentPlacementModel: Placement

    private var _currentPlacement: MutableLiveData<Event<Placement>> = MutableLiveData()
    val currentPlacement: LiveData<Event<Placement>> = _currentPlacement

    public fun setCurrentPlacement(placement: Placement) {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()
            currentPlacementModel = placement
            _currentPlacement.postValue(Event(currentPlacementModel))
        }
    }
}