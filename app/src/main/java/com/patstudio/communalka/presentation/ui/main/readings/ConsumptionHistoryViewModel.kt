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

class ConsumptionHistoryViewModel(private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentMeter: PlacementMeter

    private var _prevScreen: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val prevScreen: LiveData<Event<Boolean>> = _prevScreen

    private var _userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val userMessage: LiveData<Event<String>> = _userMessage

    private var _consumptionHistory: MutableLiveData<Event<ArrayList<ConsumptionHistory>>> = MutableLiveData()
    val consumptionHistory: LiveData<Event<ArrayList<ConsumptionHistory>>> = _consumptionHistory

    private var _updatePosition: MutableLiveData<Event<Int>> = MutableLiveData()
    val updatePosition: LiveData<Event<Int>> = _updatePosition

    fun setCurrentPlacementMeter(currentMeter: PlacementMeter) {
        this.currentMeter = currentMeter
        getMeterHistory()
    }

    private fun getMeterHistory() {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getMeterHistory(currentMeter.id)
                .onStart {}
                .collect {
                    when (it) {
                        is Result.Success -> {
                            val turnsType = object : TypeToken<ArrayList<ConsumptionHistory>>() {}.type
                            var consumptionHistory: ArrayList<ConsumptionHistory> = gson.fromJson(it.data.data, turnsType)
                            consumptionHistory.map { parent ->
                                parent.isOpened = true
                                parent.children.map {
                                    parent.isOpened = true
                                }
                            }
                            _consumptionHistory.postValue(Event(consumptionHistory))
                        }
                    }
                }
        }
    }

    fun updateOpenedTable(consumptionHistory: ConsumptionHistory, position: Int) {
        consumptionHistory.isOpened = !consumptionHistory.isOpened!!
        _updatePosition.postValue(Event(position))
    }

}