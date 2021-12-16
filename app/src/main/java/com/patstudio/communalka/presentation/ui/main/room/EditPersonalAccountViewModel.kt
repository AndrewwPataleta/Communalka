package com.patstudio.communalka.presentation.ui.main.room

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
import kotlinx.coroutines.launch

class EditPersonalAccountViewModel(private val userRepository: UserRepository, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson): ViewModel() {

    private lateinit var user: User
    private lateinit var currentService: Service
    private lateinit var suppliers: List<Supplier>
    private lateinit var currentPlacement: Placement
    private  var personalCounters: ArrayList<PersonalCounter> = java.util.ArrayList()
    private  var personalNumberError:  MutableLiveData<Event<String>> = MutableLiveData()
    private val progressConnectPersonalNumber: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val openPersonalAccountsPage: MutableLiveData<Event<Placement>> = MutableLiveData()
    private val removeServiceDialog: MutableLiveData<Event<Service>> = MutableLiveData()
    private val service: MutableLiveData<Event<Service>> = MutableLiveData()
    private val supplierList: MutableLiveData<Event<List<Supplier>>> = MutableLiveData()
    private var personalCounter: MutableLiveData<Event<List<PersonalCounter>>> = MutableLiveData()
    private lateinit var selectedSupplier: Supplier
    private var personalNumber: String = ""

    private var _supplierName: MutableLiveData<String> = MutableLiveData()
    val supplierName: LiveData<String> = _supplierName

    fun setSelectedPosition(position: Int) {
        selectedSupplier = suppliers.get(position)
    }

    private fun getListPersonalAccounts() {
        viewModelScope.launch(dispatcherProvider.io) {

            userRepository.getMeters(currentService.account.id)
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            val turnsType = object : TypeToken<List<PersonalCounter>>() {}.type
                            personalCounters = gson.fromJson(it.data.data!!.asJsonObject.get("meters"), turnsType)
                            personalCounter.postValue(Event(personalCounters))
                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }
                }

            userRepository.getSuppliers("")
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Success -> {

                            val turnsType = object : TypeToken<List<Supplier>>() {}.type
                            var suppliers: ArrayList<Supplier> = gson.fromJson(it.data.data!!.asJsonObject.get("suppliers"), turnsType)
                            suppliers.map {
                                if (currentService.account.supplier!!.compareTo(it.id) == 0) {
                                    _supplierName.postValue(it.name)
                                }
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

    fun removeCounter(selectedPersonalCounter: PersonalCounter) {

        viewModelScope.launch(dispatcherProvider.io) {

            if (selectedPersonalCounter.id != null) {
                userRepository.removeMeter(selectedPersonalCounter.id!!)
                    .catch { it.printStackTrace() }
                    .collect {

                    }
            }
            personalCounters.remove(selectedPersonalCounter)
            personalCounter.postValue(Event(personalCounters))
        }
    }

    fun removeCurrentPersonalAccount() {
        removeServiceDialog.postValue(Event(currentService))
    }

    private fun createMeter(personalCounter: PersonalCounter, accountId: String,  serviceName: String?) {
        if (personalCounter.serial_number?.length == 0 || personalCounter.title.length == 0) {
            if (personalCounters.size > 0) {
                val personCounter = personalCounters.removeLast()
                if (personCounter.id == null) {
                    createMeter(personCounter, currentService.account.id,serviceName)
                } else {
                    editMeter(personCounter, currentService.account.id, serviceName)
                }
            } else {
                openPersonalAccountsPage.postValue(Event(currentPlacement))
            }
        } else {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.createMeter(personalCounter.title, personalCounter.serial_number!!, personalCounter.value, accountId)
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                if (personalCounters.size > 0) {
                                    val personCounter = personalCounters.removeLast()
                                    if (personCounter.id == null) {
                                        createMeter(personCounter, currentService.account.id, serviceName)
                                    } else {
                                        editMeter(personCounter, currentService.account.id, serviceName)
                                    }
                                } else {
                                    openPersonalAccountsPage.postValue(Event(currentPlacement))
                                }
                            }
                            is Result.ErrorResponse -> { }
                            is Result.Error -> { }
                        }
                    }
            }
        }
    }

    private fun editMeter(personalCounter: PersonalCounter, accountId: String, serviceName: String?) {
        if (personalCounter.serial_number?.length == 0 || personalCounter.title.length == 0) {
            if (personalCounters.size > 0) {
                val personCounter = personalCounters.removeLast()
                if (personCounter.id == null) {
                    createMeter(personCounter, currentService.account.id,serviceName)
                } else {
                    editMeter(personCounter, currentService.account.id,serviceName)
                }
            } else {
                openPersonalAccountsPage.postValue(Event(currentPlacement))
            }
        } else {
            viewModelScope.launch(dispatcherProvider.io) {
                var serialNumber = ""
                if (personalCounter.serial_number != null) {
                    serialNumber = personalCounter.serial_number!!
                }
                userRepository.editMeter(personalCounter.title, serialNumber, null, personalCounter.id!!)
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                if (personalCounters.size > 0) {
                                    val personCounter = personalCounters.removeLast()
                                    if (personCounter.id == null) {
                                        createMeter(personCounter, currentService.account.id,serviceName)
                                    } else {
                                        editMeter(personCounter, currentService.account.id,serviceName )
                                    }
                                } else {
                                    openPersonalAccountsPage.postValue(Event(currentPlacement))
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
        if (personalCounters.size > 0) {
            viewModelScope.launch(dispatcherProvider.io) {
                val personCounter = personalCounters.removeLast()
                    if (personCounter.id == null) {
                        createMeter(personCounter, currentService.account.id, "")
                    } else {
                        editMeter(personCounter, currentService.account.id, "")
                    }
                }
            } else {
                openPersonalAccountsPage.postValue(Event(currentPlacement))
        }
    }

    fun validate(): Boolean {
        var isValidate = true
        if (personalNumber.length == 0) {
            personalNumberError.postValue(Event("Поле не может быть пустым"))
            isValidate = false
        }
        return isValidate
    }

    fun confirmRemovePersonalAccount() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.deleteAccount(currentService.account.id)
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Loading -> { }
                        is Result.Success -> { }
                        is Result.ErrorResponse -> { }
                        is Result.Error -> {
                            openPersonalAccountsPage.postValue(Event(currentPlacement))
                        }
                    }

                }
        }
    }

    fun getPersonalAccount(): MutableLiveData<Event<Service>> {
        return service
    }

    fun getOpenPersonalAccountsPage(): MutableLiveData<Event<Placement>> {
        return openPersonalAccountsPage
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

    fun getRemovePersonalAccountDialog(): MutableLiveData<Event<Service>> {
        return removeServiceDialog
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