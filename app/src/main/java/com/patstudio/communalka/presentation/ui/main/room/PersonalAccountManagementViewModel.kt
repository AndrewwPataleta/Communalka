package com.patstudio.communalka.presentation.ui.main.room

import android.net.Uri
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
import com.patstudio.communalka.data.model.auth.ConfirmSmsWrapper
import com.patstudio.communalka.data.repository.premises.DaDataRepository
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalAccountManagementViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository,  private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentPlacement: Placement
    private val unconnectedPersonalAccount: MutableLiveData<Event<List<PersonalAccount>>> = MutableLiveData()
    private val connectedPersonalAccount: MutableLiveData<Event<List<PersonalAccount>>> = MutableLiveData()
    private val personalAccountForConnect: MutableLiveData<Event<Pair<PersonalAccount,Placement>>> = MutableLiveData()
    private var unconnectedPersonalAccountList: ArrayList<PersonalAccount> = ArrayList()
    private var connectedPersonalAccountList: ArrayList<PersonalAccount> = ArrayList()

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
                            unconnectedPersonalAccountList = ArrayList()
                            connectedPersonalAccountList = ArrayList()
                            val turnsType = object : TypeToken<List<PersonalAccount>>() {}.type
                            var personalAccounts: List<PersonalAccount> = gson.fromJson(it.data.data!!.asJsonObject.get("services"), turnsType)
                            personalAccounts.map {
                                if (it.account != null) {
                                    connectedPersonalAccountList.add(it)
                                } else {
                                    unconnectedPersonalAccountList.add(it)
                                }
                            }
                            if (unconnectedPersonalAccountList.size > 0)
                                unconnectedPersonalAccount.postValue(Event(unconnectedPersonalAccountList))
                            if (connectedPersonalAccountList.size > 0)
                                connectedPersonalAccount.postValue(Event(connectedPersonalAccountList))
                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }
                }
        }
    }

    public fun setCurrentRoom(placement: Placement) {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()
            currentPlacement = placement;
            _subTitlePlacement.postValue(Event(currentPlacement.name))
            getListPersonalAccounts()
        }
    }

    fun selectConnectPersonalAccount(model: PersonalAccount) {

        personalAccountForConnect.postValue(Event(Pair(model,currentPlacement)))
    }

    fun getUnconnectedPersonalAccount(): MutableLiveData<Event<List<PersonalAccount>>> {
        return unconnectedPersonalAccount
    }

    fun getConnectedPersonalAccount(): MutableLiveData<Event<List<PersonalAccount>>> {
        return connectedPersonalAccount
    }

    fun getPersonalAccountForConnect(): MutableLiveData<Event<Pair<PersonalAccount,Placement>>> {
        return personalAccountForConnect
    }
}