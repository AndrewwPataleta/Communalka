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
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransmissionReadingListViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentService: Service
    private lateinit var currentPlacementModel: Placement
    private lateinit var placementMetersList: ArrayList<PlacementMeter>

    private var _placementMeters: MutableLiveData<Event<ArrayList<PlacementMeter>>> = MutableLiveData()
    val placementMeters: LiveData<Event<ArrayList<PlacementMeter>>> = _placementMeters

    private var _currentPlacement: MutableLiveData<Event<Placement>> = MutableLiveData()
    val currentPlacement: LiveData<Event<Placement>> = _currentPlacement

    private var _transmissionPlacementMeter: MutableLiveData<Event<PlacementMeter>> = MutableLiveData()
    val transmissionPlacementMeter: LiveData<Event<PlacementMeter>> = _transmissionPlacementMeter

    private var _historyPlacementMeter: MutableLiveData<Event<PlacementMeter>> = MutableLiveData()
    val historyPlacementMeter: LiveData<Event<PlacementMeter>> = _historyPlacementMeter

    fun setCurrentPlacement(placement: Placement) {
        try {
            viewModelScope.launch(dispatcherProvider.io) {
                user = userRepository.getLastAuthUser()
                currentPlacementModel = placement

                _currentPlacement.postValue(Event(currentPlacementModel))

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateMeters() {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getPlacementDetail(currentPlacementModel.id)
                .collect {

                    when (it) {
                        is Result.Success -> {
                            var placement = gson.fromJson(it.data.data!!.asJsonObject.get("placement"), Placement::class.java)
                            placementMetersList = ArrayList()
                            placement.accounts.map {
                                it.meters.map {
                                    placementMetersList.add(it)
                                }
                            }
                            _placementMeters.postValue(Event(placementMetersList))
                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }
                }
        }
    }

    fun selectMeterForHistory(placementMeter: PlacementMeter) {
        _historyPlacementMeter.postValue(Event(placementMeter))
    }

    public fun selectMeterForTransmissionReading(placementMeter: PlacementMeter) {
        _transmissionPlacementMeter.postValue(Event(placementMeter))
    }
}