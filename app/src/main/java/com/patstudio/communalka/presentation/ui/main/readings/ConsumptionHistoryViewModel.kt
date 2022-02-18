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

class ConsumptionHistoryViewModel(private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentMeter: PlacementMeter

    private var _prevScreen: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val prevScreen: LiveData<Event<Boolean>> = _prevScreen

    private var _userMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val userMessage: LiveData<Event<String>> = _userMessage

    private var _pdfBytes: MutableLiveData<Event<ByteArray>> = MutableLiveData()
    val pdfBytes: LiveData<Event<ByteArray>> = _pdfBytes

    private var _consumptionHistory: MutableLiveData<Event<ArrayList<ConsumptionHistory>>> = MutableLiveData()
    val consumptionHistory: LiveData<Event<ArrayList<ConsumptionHistory>>> = _consumptionHistory

    private var _updatePosition: MutableLiveData<Event<Int>> = MutableLiveData()
    val updatePosition: LiveData<Event<Int>> = _updatePosition


    private var _pdfDownload: MutableLiveData<Event<Pair<String,String>>> = MutableLiveData()
    val pdfDownload: LiveData<Event<Pair<String,String>>> = _pdfDownload

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

    fun downloadByPdf() {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getMeterPdf(currentMeter.id)
                .onStart {}
                .collect {
                   _pdfBytes.postValue(Event(it.bytes()))
                }
        }
    }

    fun selectDownloadPdf(consumptionHistory: ConsumptionHistory) {


        _pdfDownload.postValue(Event(Pair(currentMeter.id, consumptionHistory.period_string)))
    }

    fun updateOpenedTable(consumptionHistory: ConsumptionHistory, position: Int) {
        consumptionHistory.isOpened = !consumptionHistory.isOpened!!
        _updatePosition.postValue(Event(position))
    }

}