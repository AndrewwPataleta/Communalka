package com.patstudio.communalka.presentation.ui.main.readings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.model.auth.ConfirmFormError
import com.patstudio.communalka.data.model.auth.LoginFormError
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TransmissionReadingsViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentService: Service
    private lateinit var currentPlacementMeter: PlacementMeter

    private var _currentPlacement: MutableLiveData<Event<PlacementMeter>> = MutableLiveData()
    val currentPlacement: LiveData<Event<PlacementMeter>> = _currentPlacement

    private var _isSendingTransmissions: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isSendingTransmissions: LiveData<Event<Boolean>> = _isSendingTransmissions

    private var _prevScreen: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val prevScreen: LiveData<Event<Boolean>> = _prevScreen

    private var _userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val userMessage: LiveData<Event<String>> = _userMessage

    public fun setCurrentMeter(placementMeter: PlacementMeter) {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()
            currentPlacementMeter = placementMeter
            _currentPlacement.postValue(Event(currentPlacementMeter))
        }
    }

    fun sendTransmissions(readings: String) {

        var reading = readings.toInt()

        if (!readings.isNullOrEmpty()) {
            viewModelScope.launch(dispatcherProvider.io) {
                var serialNumber = ""
                if (currentPlacementMeter.serial_number != null) {
                    serialNumber = currentPlacementMeter.serial_number!!
                }
                userRepository.editMeter(currentPlacementMeter.title, serialNumber, readings.toInt().toString(), currentPlacementMeter.id)
                    .onStart { _isSendingTransmissions.postValue(Event(true)) }
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                _isSendingTransmissions.postValue(Event(false))
                                _prevScreen.postValue(Event(true))
                            }
                            is Result.Error -> {

                                _isSendingTransmissions.postValue(Event(false))

                            }
                            is Result.ErrorResponse -> {
                                _isSendingTransmissions.postValue(Event(false))
                                when(it.data.status) {
                                    "error" -> {
                                        it.data.message?.let {
                                            _userMessage.postValue(Event(it))
                                        }
                                    }
                                }
                            }
                        }

                    }

            }
        } else {
            Log.d("readings", " null ")
        }
    }
}