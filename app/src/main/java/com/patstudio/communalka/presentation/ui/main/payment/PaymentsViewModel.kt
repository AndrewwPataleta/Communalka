package com.patstudio.communalka.presentation.ui.main.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patstudio.communalka.common.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.model.auth.LoginResponseError
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import convertLongToFilterTime
import isEmailValid
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import com.huxq17.download.core.DownloadInfo

import android.R.id

import com.huxq17.download.Pump





class PaymentsViewModel(private val userRepository: UserRepository, private val gson: Gson, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private var _payments: MutableLiveData<Event<List<PaymentHistoryModel>>> = MutableLiveData()
    val payments: LiveData<Event<List<PaymentHistoryModel>>> = _payments

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    private var _openFilter: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val openFilter: LiveData<Event<Boolean>> = _openFilter

    private var _updateLists: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val updateLists: LiveData<Event<Boolean>> = _updateLists

    private var _filterModel: MutableLiveData<Event<PaymentFilterModel>> = MutableLiveData()
    val filterModel: LiveData<Event<PaymentFilterModel>> = _filterModel

    private var _totalPaymentAmount: MutableLiveData<Event<Float>> = MutableLiveData()
    val totalPaymentAmount: LiveData<Event<Float>> = _totalPaymentAmount

    private var _confirmFilterModel: MutableLiveData<Event<PaymentFilterModel>> = MutableLiveData()
    val confirmFilterModel: LiveData<Event<PaymentFilterModel>> = _confirmFilterModel

    private var _actionReceipt: MutableLiveData<Event<PaymentHistoryModel>> = MutableLiveData()
    val actionReceipt: LiveData<Event<PaymentHistoryModel>> = _actionReceipt

    private var _dialogEmailSend: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val dialogEmailSend: LiveData<Event<Boolean>> = _dialogEmailSend

    private var _dialogEmailSendError: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val dialogEmailSendError: LiveData<Event<Boolean>> = _dialogEmailSendError

    private var _toastMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val toastMessage: LiveData<Event<String>> = _toastMessage


    private var _backScreen: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val backScreen: LiveData<Event<Boolean>> = _backScreen

    private var filter: PaymentFilterModel? = null

    private var confirmFilter: PaymentFilterModel? = null

    fun openFilter() {
        _openFilter.postValue(Event(true))
    }


    private fun uploadFilter() {
        viewModelScope.launch(dispatcherProvider.io) {
            userRepository.getSuppliers("")
                .catch { it.printStackTrace() }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            val turnsType = object : TypeToken<List<Supplier>>() {}.type
                            var suppliers: ArrayList<Supplier> = gson.fromJson(
                                it.data.data!!.asJsonObject.get("suppliers"),
                                turnsType
                            )
                            roomRepository.getUserPremises()
                                .catch {
                                    it.printStackTrace()
                                }
                                .collect {
                                    when (it) {
                                        is Result.Success -> {
                                            var placementList: PlacementWrapper = gson.fromJson(it.data.data, PlacementWrapper::class.java)
                                            userRepository.getServices()
                                                .catch {
                                                    it.printStackTrace()
                                                }
                                                .collect {
                                                    when (it) {
                                                        is Result.Success -> {
                                                            val turnsType = object : TypeToken<List<Service>>() {}.type
                                                            var services: ArrayList<Service> = gson.fromJson(it.data.data!!.asJsonObject.get("services"), turnsType)
                                                            filter = PaymentFilterModel(null, suppliers = Pair(true,suppliers), placement = Pair(true,placementList.placements), services = Pair(true,services))
                                                            _filterModel.postValue(Event(filter!!))
                                                            getPaymentHistory()
                                                        }
                                                        is Result.ErrorResponse -> { }
                                                        is Result.Error -> { }
                                                    }
                                                }
                                        }
                                        is Result.ErrorResponse -> { }
                                        is Result.Error -> { }
                                    }
                                }
                        }
                        is Result.Error -> { }
                        is Result.ErrorResponse -> { }
                    }
                }
        }
    }

    fun updateFilters() {
        if (filter == null) {
            uploadFilter()
        } else {
            _filterModel.postValue(Event(filter!!))
            getPaymentHistory()
        }
    }



    fun selectDownload() {

    }

    fun selectEmail() {
        _dialogEmailSend.postValue(Event(true))
    }

    fun selectOpen() {

    }

    fun resetFilter() {
        if (filter != null) {
            filter!!.date = null
            confirmFilter?.date = null
            filter!!.placement.second.map {
                it.selected = false
            }
            filter!!.suppliers.second.map {
                it.selected = false
            }
            filter!!.services.second.map {
                it.selected = false
            }
            _filterModel.postValue(Event(filter!!))
        }
    }


    fun updateSupplierVisible() {
        if (filter != null) {
            filter!!.suppliers = Pair(!filter!!.suppliers.first, filter!!.suppliers.second)
            _filterModel.postValue(Event(filter!!))
        }
    }


    fun acceptFilter() {
        confirmFilter = filter?.copy()
        _confirmFilterModel.postValue(Event(confirmFilter!!))
        _backScreen.postValue(Event(true))
    }

    fun updateServiceVisible() {
        if (filter != null) {
            filter!!.services = Pair(!filter!!.services.first, filter!!.services.second)
            _filterModel.postValue(Event(filter!!))
        }

    }

    fun updatePlacementVisible() {
        if (filter != null) {
            filter!!.placement = Pair(!filter!!.placement.first, filter!!.placement.second)
            _filterModel.postValue(Event(filter!!))
        }
    }

    fun sendReceiptTo(email: String) {
        if (email.isEmailValid()) {

            var placemets = confirmFilter?.placement?.second?.filter { it.selected }
            var placementsId = placemets?.map {
                it.id
            }

            var suppliers = confirmFilter?.suppliers?.second?.filter { it.selected }
            var suppliersId = suppliers?.map {
                it.id
            }

            var services = confirmFilter?.services?.second?.filter { it.selected }
            var servicesId = services?.map {
                it.id
            }

            var convertedDateStart = confirmFilter?.date?.first?.let {
                convertLongToFilterTime(it)
            }

            var convertedDateEnd = confirmFilter?.date?.second?.let {
                convertLongToFilterTime(it)
            }

            viewModelScope.launch(dispatcherProvider.io) {
                userRepository.sendReceiptToEmail(convertedDateStart, convertedDateEnd, placement = placementsId, services = servicesId, suppliers = suppliersId, email)
                    .catch {
                        it.printStackTrace()
                    }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                _toastMessage.postValue(Event("Чеки отправлены на почту"))

                            }
                            is Result.Error -> {

                            }
                            is Result.ErrorResponse -> {
                                when(it.data.status) {
                                    "fail" -> {
                                        _toastMessage.postValue(Event(it.data.message))
                                    }
                                }
                        } }

                    }
            }
        } else {
            _dialogEmailSendError.postValue(Event(true))
        }

    }

    fun selectActionReceipt(model: PaymentHistoryModel) {
        _actionReceipt.postValue(Event(model))
    }

    fun updateFilterDate(date: androidx.core.util.Pair<Long, Long>) {
        filter!!.date = date
        _filterModel.postValue(Event(filter!!))
    }

    fun changeSelectedFilterSupplier(supplier: Supplier, checked: Boolean) {
        supplier.selected = checked
        _updateLists.postValue(Event(true))
    }

    fun onCheckedSupplierChange(supplier: Supplier) {

    }

    fun removeDateFromFilter() {
        filter!!.date = null
        confirmFilter!!.date = null
        _filterModel.postValue(Event(filter!!))
        _confirmFilterModel.postValue(Event(confirmFilter!!))
        getPaymentHistory()
    }

    fun removePlacementFromFilter(placement: Placement) {
        placement.selected = false
        _confirmFilterModel.postValue(Event(confirmFilter!!))
        _filterModel.postValue(Event(filter!!))
        getPaymentHistory()
    }

    fun removeSupplierFromFilter(supplier: Supplier) {
        supplier.selected = false
        _confirmFilterModel.postValue(Event(confirmFilter!!))
        _filterModel.postValue(Event(filter!!))
        getPaymentHistory()
    }

    fun removeServiceFromFilter(service: Service) {
        service.selected = false
        _confirmFilterModel.postValue(Event(confirmFilter!!))
        _filterModel.postValue(Event(filter!!))
        getPaymentHistory()
    }

    fun changeSelectedFilterPlacement(placement: Placement, checked: Boolean) {
        placement.selected = checked
        _updateLists.postValue(Event(true))
    }

    fun changeSelectedFilterService(service: Service, checked: Boolean) {
        service.selected = checked
        _updateLists.postValue(Event(true))
    }

    private fun getPaymentHistory() {
        viewModelScope.launch(dispatcherProvider.io) {
            var placemets = confirmFilter?.placement?.second?.filter { it.selected }
            var placementsId = placemets?.map {
                it.id
            }

            var suppliers = confirmFilter?.suppliers?.second?.filter { it.selected }
            var suppliersId = suppliers?.map {
                it.id
            }

            var services = confirmFilter?.services?.second?.filter { it.selected }
            var servicesId = services?.map {
                it.id
            }

            var convertedDateStart = confirmFilter?.date?.first?.let {
                convertLongToFilterTime(it)
            }

            var convertedDateEnd = confirmFilter?.date?.second?.let {
                convertLongToFilterTime(it)
            }

            roomRepository.getPaymentsHistory(convertedDateStart, convertedDateEnd, placement = placementsId, services = servicesId, suppliers = suppliersId)
                .onStart {
                    _showProgress.postValue(Event(true))
                }
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    when (it) {
                        is Result.Success -> {
                            _showProgress.postValue(Event(false))
                            val type = object : TypeToken<List<PaymentHistoryModel>>() {}.type
                            var paymentsList: List<PaymentHistoryModel> = gson.fromJson(it.data.data!!.asJsonObject.get("orders"), type)
                            var total = 0.0
                            paymentsList.map {
                                total += (it.taxAmount+it.amount)
                            }

                            _totalPaymentAmount.postValue(Event(total.toFloat()))
                            _payments.postValue(Event(paymentsList))

                        }
                        is Result.Error -> {

                        }
                        is Result.ErrorResponse -> {

                        }
                    } }
        }
    }

}