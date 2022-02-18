package com.patstudio.communalka.presentation.ui.main.readings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.repository.premises.RoomRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AccrualViewModel(private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentService: String
    private lateinit var currentPlacement: String
    private lateinit var currentMeter: String
    private lateinit var account: Account


    private var _historyPlacementMeter: MutableLiveData<Event<Triple<PlacementMeter, String, String>>> = MutableLiveData()
    val historyPlacementMeter: LiveData<Event<Triple<PlacementMeter, String, String>>> = _historyPlacementMeter

    var _meters: MutableLiveData<Pair<Account, ArrayList<PlacementMeter>>> = MutableLiveData()
    val meters: LiveData<Pair<Account, ArrayList<PlacementMeter>>> = _meters

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
            roomRepository.getAccount(currentService)
                .onStart {}
                .collect {
                    when (it) {
                        is Result.Success -> {
                            account = gson.fromJson(it.data.data!!.asJsonObject.get("account"), Account::class.java)
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
                                                            _meters.postValue(Pair(account,meters))
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                }
                        }
                    }
                }

        }
    }


}