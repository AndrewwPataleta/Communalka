package com.patstudio.communalka.presentation.ui.main.room

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
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PersonalAccountManagementViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentPlacement: Placement
    private val unconnectedService: MutableLiveData<Event<Pair<List<Service>, Boolean>>> = MutableLiveData()
    private val connectedService: MutableLiveData<Event<List<Service>>> = MutableLiveData()
    private val serviceForConnect: MutableLiveData<Event<Pair<Service,Placement>>> = MutableLiveData()
    private var unconnectedServiceList: ArrayList<Service> = ArrayList()
    private var connectedServiceList: ArrayList<Service> = ArrayList()

    private var firstServices = false

    private var _subTitlePlacement: MutableLiveData<Event<String>> = MutableLiveData()
    val subTitlePlacement: LiveData<Event<String>> = _subTitlePlacement


    private fun getListPersonalAccounts() {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getPlacementPersonalAccount(currentPlacement)
                .catch { it.printStackTrace() }
                .collect(

                )
            roomRepository.getServicesPlacement(currentPlacement)
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            unconnectedServiceList = ArrayList()
                            connectedServiceList = ArrayList()
                            val turnsType = object : TypeToken<List<Service>>() {}.type
                            var services: List<Service> = gson.fromJson(it.data.data!!.asJsonObject.get("services"), turnsType)
                            services.map {
                                if (it.account != null) {
                                    connectedServiceList.add(it)
                                } else {
                                    unconnectedServiceList.add(it)
                                }
                            }
                            if (unconnectedServiceList.size > 0)
                                unconnectedService.postValue(Event(Pair(unconnectedServiceList,firstServices)))
                                firstServices = false
                            if (connectedServiceList.size > 0)
                                connectedService.postValue(Event(connectedServiceList))
                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }
                }
        }
    }

    public fun setFirstServices(firstServices: Boolean) {
        this.firstServices = firstServices
    }


    public fun setCurrentRoom(placement: Placement) {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()
            currentPlacement = placement;
            _subTitlePlacement.postValue(Event(currentPlacement.name))
            getListPersonalAccounts()
        }
    }

    fun selectConnectPersonalAccount(model: Service) {

        serviceForConnect.postValue(Event(Pair(model,currentPlacement)))
    }

    fun getUnconnectedPersonalAccount(): MutableLiveData<Event<Pair<List<Service>,Boolean>>> {
        return unconnectedService
    }

    fun getConnectedPersonalAccount(): MutableLiveData<Event<List<Service>>> {
        return connectedService
    }

    fun getPersonalAccountForConnect(): MutableLiveData<Event<Pair<Service,Placement>>> {
        return serviceForConnect
    }
}