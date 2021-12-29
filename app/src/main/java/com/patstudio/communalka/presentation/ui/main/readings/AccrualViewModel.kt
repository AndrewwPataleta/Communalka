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
import com.patstudio.communalka.data.model.auth.ConfirmFormError
import com.patstudio.communalka.data.model.auth.ConfirmSmsWrapper
import com.patstudio.communalka.data.model.auth.LoginFormError
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AccrualViewModel(private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentService: String
    private lateinit var currentPlacement: String
    private lateinit var currentMeter: String

    private var _historyPlacementMeter: MutableLiveData<Event<Triple<PlacementMeter, String, String>>> = MutableLiveData()
    val historyPlacementMeter: LiveData<Event<Triple<PlacementMeter, String, String>>> = _historyPlacementMeter

    var _meters: MutableLiveData<ArrayList<PlacementMeter>> = MutableLiveData()
    val meters: LiveData<ArrayList<PlacementMeter>> = _meters

    fun setCurrentService(currentMeter: String) {
        this.currentService = currentMeter
    }

    fun setCurrentMeter(currentMeter: String) {
        this.currentMeter = currentMeter
        getMeterHistory()
    }

    fun setCurrentPlacement(currentPlacement: String) {
        this.currentPlacement = currentPlacement
    }

    fun selectMeterForHistory(placementMeter: PlacementMeter) {
        _historyPlacementMeter.postValue(Event(Triple(placementMeter, currentMeter, currentPlacement)))
    }

    private fun getMeterHistory() {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getAccrual(currentService)
                .onStart {}
                .collect {
                    when (it) {
                        is Result.Success -> {
                            roomRepository.getMetersByAccount(currentService)
                                .onStart {}
                                .collect {
                                    when (it) {
                                        is Result.Success -> {
                                            val type = object : TypeToken<List<PlacementMeter>>() {}.type
                                            var meters: ArrayList<PlacementMeter> = gson.fromJson(it.data.data!!.asJsonObject.get("meters"), type)
                                            _meters.postValue(meters)
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }


}