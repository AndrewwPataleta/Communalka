package com.patstudio.communalka.presentation.ui.main.readings

import android.util.Log
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

class TransmissionReadingsViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentPersonalAccount: PersonalAccount
    private lateinit var currentPlacementMeter: PlacementMeter

    private var _currentPlacement: MutableLiveData<Event<PlacementMeter>> = MutableLiveData()
    val currentPlacement: LiveData<Event<PlacementMeter>> = _currentPlacement

    private var _isSendingTransmissions: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isSendingTransmissions: LiveData<Event<Boolean>> = _isSendingTransmissions

    public fun setCurrentMeter(placementMeter: PlacementMeter) {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()
            currentPlacementMeter = placementMeter
            _currentPlacement.postValue(Event(currentPlacementMeter))
        }
    }

    fun sendTransmissions(readings: String) {
        if (!readings.isNullOrEmpty()) {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.editMeter(currentPlacementMeter.title, currentPlacementMeter.serial_number, readings, currentPlacementMeter.id)
                    .onStart { _isSendingTransmissions.postValue(Event(true)) }
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                _isSendingTransmissions.postValue(Event(false))
                            }
                            is Result.Error -> {

                            }
                            is Result.ErrorResponse -> {

                            }
                        }

                    }

            }
        } else {
            Log.d("readings", " null ")
        }
    }
}