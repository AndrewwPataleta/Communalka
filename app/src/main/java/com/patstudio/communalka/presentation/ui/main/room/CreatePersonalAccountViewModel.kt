package com.patstudio.communalka.presentation.ui.main.room

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
    private lateinit var currentPersonalAccount: PersonalAccount
    private lateinit var suppliers: List<Supplier>
    private lateinit var currentPlacement: Placement
    private  var personalCounters: ArrayList<PersonalCounter> = java.util.ArrayList()
    private  var personalNumberError:  MutableLiveData<Event<String>> = MutableLiveData()
    private val progressConnectPersonalNumber: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val personalAccount: MutableLiveData<Event<PersonalAccount>> = MutableLiveData()
    private val supplierList: MutableLiveData<Event<List<Supplier>>> = MutableLiveData()
    private var personalCounter: MutableLiveData<Event<List<PersonalCounter>>> = MutableLiveData()
    private lateinit var selectedSupplier: Supplier
    private var personalNumber: String = ""
    private val openPersonalAccountsPage: MutableLiveData<Event<Placement>> = MutableLiveData()
    fun setSelectedPosition(position: Int) {
        selectedSupplier = suppliers.get(position)
    }

    private fun getListPersonalAccounts() {
        viewModelScope.launch(dispatcherProvider.io) {

            userRepository.getSuppliers()
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            val turnsType = object : TypeToken<List<Supplier>>() {}.type
                            suppliers = gson.fromJson(it.data.data!!.asJsonObject.get("suppliers"), turnsType)
                            supplierList.postValue(Event(suppliers))
                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    }

                }
            personalAccount.postValue(Event(currentPersonalAccount))
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

    private fun createMeter(personalCounter: PersonalCounter, accountId: String) {
        if (personalCounter.serial_number.length == 0 || personalCounter.title.length == 0) {
            if (personalCounters.size > 0) {
                createMeter(personalCounters.removeLast(),accountId)
            } else {
                openPersonalAccountsPage.postValue(Event(currentPlacement))
            }
        } else {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.createMeter(personalCounter.title, personalCounter.serial_number, personalCounter.value, accountId)
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                if (personalCounters.size > 0) {
                                    createMeter(personalCounters.removeLast(),accountId)
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
        if (validate()) {
            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.createPersonalAccount(personalNumber,user.name,selectedSupplier.id,
                    currentPersonalAccount.id, currentPlacement.id)
                    .onStart { progressConnectPersonalNumber.postValue(Event(true)) }
                    .catch { it.printStackTrace() }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                var account = gson.fromJson(it.data.data!!.asJsonObject.get("account"), Account::class.java)
                                progressConnectPersonalNumber.postValue(Event(false))
                                if (personalCounters.size > 0) {
                                    createMeter(personalCounters.removeLast(),account.id)
                                } else {
                                    openPersonalAccountsPage.postValue(Event(currentPlacement))
                                }
                            }
                            is Result.ErrorResponse -> {
                                when(it.data.status) {
                                    "fail" -> { }
                                }
                            }
                            is Result.Error -> {

                            }
                        }
                    }
            }
        }
    }

    fun getOpenPersonalAccountsPage(): MutableLiveData<Event<Placement>> {
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

    fun getPersonalAccount(): MutableLiveData<Event<PersonalAccount>> {
        return personalAccount
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

    public fun setPersonalAccount(personalAccount: PersonalAccount) {
        currentPersonalAccount = personalAccount;
    }


}