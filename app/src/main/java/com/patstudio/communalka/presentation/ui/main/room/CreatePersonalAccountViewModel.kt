package com.patstudio.communalka.presentation.ui.main.room

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

class CreatePersonalAccountViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentService: Service
    private lateinit var suppliers: ArrayList<Supplier>
    private lateinit var filtred: ArrayList<Supplier>
    private lateinit var currentPlacement: Placement
    private  var personalCounters: ArrayList<PersonalCounter> = java.util.ArrayList()
    private  var personalNumberError:  MutableLiveData<Event<String>> = MutableLiveData()
    private val progressConnectPersonalNumber: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val service: MutableLiveData<Event<Service>> = MutableLiveData()
    private val supplierList: MutableLiveData<Event<List<Supplier>>> = MutableLiveData()
    private var personalCounter: MutableLiveData<Event<List<PersonalCounter>>> = MutableLiveData()

    private var _userMessage: MutableLiveData<String> = MutableLiveData()
    val userMessage: LiveData<String> = _userMessage

    private var _disableCreate: MutableLiveData<Boolean> = MutableLiveData()
    val disableCreate: LiveData<Boolean> = _disableCreate

    private lateinit var selectedSupplier: Supplier
    private var personalNumber: String = ""
    private val openPersonalAccountsPage: MutableLiveData<Event<Pair<Placement?, String?>>> = MutableLiveData()
    fun setSelectedPosition(position: Int) {
        selectedSupplier = suppliers.get(position)
        Log.d("CreatePersonal", "selected ${selectedSupplier.name} id ${selectedSupplier.id}")
    }

    private fun getListPersonalAccounts() {
        viewModelScope.launch(dispatcherProvider.io) {

            userRepository.getSuppliers(currentService.id)
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Success -> {

                            val turnsType = object : TypeToken<List<Supplier>>() {}.type
                            suppliers = gson.fromJson(it.data.data!!.asJsonObject.get("suppliers"), turnsType)

                            var filtred: ArrayList<Supplier> = ArrayList()

                                suppliers.map {
                                    if (it.service.compareTo(currentService.id) == 0)
                                        filtred.add(it)
                                }
                            suppliers = ArrayList()
                            suppliers.addAll(filtred)
                            if (filtred.size > 0) {
                                supplierList.postValue(Event(filtred))
                            } else {
                                _disableCreate.postValue(true)
                            }
                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }

                }
            service.postValue(Event(currentService))
        }
    }

    fun addNewCounter() {
        personalCounters.add(PersonalCounter("","", ""))
        personalCounter.postValue(Event(personalCounters))
    }

    fun removeCounter(personalCounter: PersonalCounter) {
        personalCounters.remove(personalCounter)
        this.personalCounter.postValue(Event(personalCounters))
    }

    private fun createMeter(personalCounter: PersonalCounter, accountId: String, serviceName: String?) {
        if (personalCounter.serial_number?.length == 0 || personalCounter.title.length == 0) {
            if (personalCounters.size > 0) {
                createMeter(personalCounters.removeLast(),accountId, serviceName)
            } else {
                openPersonalAccountsPage.postValue(Event(Pair(currentPlacement, serviceName)))
            }
        } else {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.createMeter(personalCounter.title, personalCounter.serial_number!!, personalCounter.value, accountId)
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                if (personalCounters.size > 0) {
                                    createMeter(personalCounters.removeLast(),accountId, serviceName)
                                } else {
                                    openPersonalAccountsPage.postValue(Event(Pair(currentPlacement, serviceName)))
                                }
                            }
                            is Result.ErrorResponse -> { }
                            is Result.Error -> { }
                        }
                    }
            }
        }
    }

    fun connectPersonalNumber() {
        if (validate()) {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.createPersonalAccount(personalNumber,user.name,selectedSupplier.id,
                    currentService.id, currentPlacement.id)
                    .onStart { progressConnectPersonalNumber.postValue(Event(true)) }
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                var account = gson.fromJson(it.data.data!!.asJsonObject.get("account"), Account::class.java)

                                account.active?.let {
                                    if (it) {

                                        if (personalCounters.size > 0) {
                                            createMeter(personalCounters.removeLast(),account.id, currentService.name)
                                        } else {
                                            openPersonalAccountsPage.postValue(Event(Pair(currentPlacement, currentService.name)))
                                        }
                                    } else if (!it && account.message.isNotEmpty()) {
                                        if (personalCounters.size > 0) {
                                            createMeter(personalCounters.removeLast(),account.id, null)
                                        } else {
                                            openPersonalAccountsPage.postValue(Event(Pair(currentPlacement, null)))
                                        }
                                        _userMessage.postValue(account.message)
                                    } else if (!it) {
                                        _userMessage.postValue("Такой лицевой счет не найден")
                                        openPersonalAccountsPage.postValue(Event(Pair(currentPlacement, null)))
                                    }
                                }
                                progressConnectPersonalNumber.postValue(Event(false))

                            }
                            is Result.ErrorResponse -> {
                                _userMessage.postValue(it.data.message)
                                progressConnectPersonalNumber.postValue(Event(false))
                            }
                            is Result.Error -> {

                            }
                        }
                    }
            }
        }
    }

    fun getOpenPersonalAccountsPage(): MutableLiveData<Event<Pair<Placement?, String?>>> {
        return openPersonalAccountsPage
    }

    fun validate(): Boolean {
        var isValidate = true
        if (personalNumber.length == 0) {
            personalNumberError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        return isValidate
    }

    fun getPersonalAccount(): MutableLiveData<Event<Service>> {
        return service
    }

    fun getSupplierList(): MutableLiveData<Event<List<Supplier>>> {
        return supplierList
    }

    fun setPersonalNumber(personalNumber: String) {
        this.personalNumber = personalNumber
    }

    fun getPersonalCounters(): MutableLiveData<Event<List<PersonalCounter>>> {
        return personalCounter
    }

    fun getPersonalNumberError(): MutableLiveData<Event<String>> {
        return personalNumberError
    }

    fun getProgressConnectPersonalNumber(): MutableLiveData<Event<Boolean>> {
        return progressConnectPersonalNumber
    }

    public fun setCurrentPlacement(placement: Placement) {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()
            currentPlacement = placement;
            getListPersonalAccounts()
        }
    }

    public fun setPersonalAccount(service: Service) {
        currentService = service;
    }


}