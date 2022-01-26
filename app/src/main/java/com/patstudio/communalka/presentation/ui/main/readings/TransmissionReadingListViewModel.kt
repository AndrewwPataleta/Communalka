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
import java.util.*
import kotlin.collections.ArrayList


class TransmissionReadingListViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentService: Service
    private lateinit var currentPlacementModel: Placement
    private lateinit var placementMetersList: ArrayList<PlacementMeter>
    private lateinit var placements: ArrayList<Placement>

    private var _placementMeters: MutableLiveData<Event<ArrayList<PlacementMeter>>> = MutableLiveData()
    val placementMeters: LiveData<Event<ArrayList<PlacementMeter>>> = _placementMeters

    private var _currentPlacement: MutableLiveData<Event<Placement>> = MutableLiveData()
    val currentPlacement: LiveData<Event<Placement>> = _currentPlacement

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    private var _progressMeters: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val progressMeters: LiveData<Event<Boolean>> = _progressMeters

    private var _placementsList: MutableLiveData<Event<ArrayList<Placement>>> = MutableLiveData()
    val placementsList: LiveData<Event<ArrayList<Placement>>> = _placementsList

    private var _transmissionPlacementMeter: MutableLiveData<Event<PlacementMeter>> = MutableLiveData()
    val transmissionPlacementMeter: LiveData<Event<PlacementMeter>> = _transmissionPlacementMeter

    private var _historyPlacementMeter: MutableLiveData<Event<Triple<PlacementMeter, String, String>>> = MutableLiveData()
    val historyPlacementMeter: LiveData<Event<Triple<PlacementMeter, String, String>>> = _historyPlacementMeter

    fun setCurrentPlacement(placement: Placement) {
        try {
            viewModelScope.launch(dispatcherProvider.io) {
                user = userRepository.getLastAuthUser()
                currentPlacementModel = placement
                _currentPlacement.postValue(Event(currentPlacementModel))
                getPlacements()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPlacements() {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getUserPremises()
                .onStart { _showProgress.postValue(Event(true)) }
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            var placementList: PlacementWrapper = gson.fromJson(it.data.data, PlacementWrapper::class.java)
                            placements = placementList.placements
                            var placementLocal = roomRepository.getUserPremises(user!!.id)

                            placements.map { parent ->
                                placementLocal.map { child ->
                                    if (parent.id.compareTo(child.id) == 0) {
                                        parent.imageType = child.imageType
                                        parent.path = child.imagePath
                                    }
                                }
                            }
                            var indexSelected = 0
                            placements.forEachIndexed { index, it ->

                                if (it.id == currentPlacementModel.id) {
                                    it.selected = true
                                    indexSelected = index
                                    currentPlacementModel = it
                                }
                            }

                            Collections.swap(placements, 0, indexSelected);
                            _placementsList.postValue(Event(placements))
                        }
                        is Result.ErrorResponse -> { }
                        is Result.Error -> { }
                    }
                }
        }
    }

    fun selectedPlacement(placement: Placement) {
        placements.map {
            it.selected = it.id.compareTo(placement.id) == 0
        }
        currentPlacementModel = placement
        updateMeters()
    }

    fun updateMeters() {

        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getPlacementDetail(currentPlacementModel.id)
                .collect {
                    when (it) {
                        is Result.Success -> {
                            var placement = gson.fromJson(it.data.data!!.asJsonObject.get("placement"), Placement::class.java)
                            placementMetersList = ArrayList()

                            roomRepository.getServicesPlacement(currentPlacementModel)
                                .catch { it.printStackTrace() }
                                .collect {
                                    when (it) {
                                        is Result.Success -> {
                                            val turnsType = object : TypeToken<List<Service>>() {}.type
                                            var services: List<Service> = gson.fromJson(it.data.data!!.asJsonObject.get("services"), turnsType)
                                            placement.accounts.map {
                                                var serviceName = ""
                                                services.map { service->
                                                    if (service.id.compareTo(it.service) == 0) {
                                                        serviceName = service.name
                                                    }
                                                }

                                                it.meters.map { meter ->
                                                    meter.serviceName = serviceName
                                                    placementMetersList.add(meter)
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
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }
                }
        }
    }

    fun selectMeterForHistory(placementMeter: PlacementMeter) {
        _historyPlacementMeter.postValue(Event(Triple(placementMeter, currentPlacementModel.name, "")))
    }

    public fun selectMeterForTransmissionReading(placementMeter: PlacementMeter) {
        _transmissionPlacementMeter.postValue(Event(placementMeter))
    }
}