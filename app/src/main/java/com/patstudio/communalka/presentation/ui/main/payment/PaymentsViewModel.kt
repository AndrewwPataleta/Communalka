package com.patstudio.communalka.presentation.ui.main.payment

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

    private var filter: PaymentFilterModel? = null

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
        }
    }

    fun resetFilter() {
        if (filter != null) {
            filter!!.date = null
            filter!!.placement.second.map {
                it.selected = false
            }
            filter!!.suppliers.second.map {
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
        _filterModel.postValue(Event(filter!!))
    }

    fun removePlacementFromFilter(placement: Placement) {
        placement.selected = false
        _filterModel.postValue(Event(filter!!))
    }

    fun removeSupplierFromFilter(supplier: Supplier) {
        supplier.selected = false
        _filterModel.postValue(Event(filter!!))
    }

    fun removeServiceFromFilter(service: Service) {
        service.selected = false
        _filterModel.postValue(Event(filter!!))
    }

    fun changeSelectedFilterPlacement(placement: Placement, checked: Boolean) {
        placement.selected = checked
        _updateLists.postValue(Event(true))
    }

    fun changeSelectedFilterService(service: Service, checked: Boolean) {
        service.selected = checked
        _updateLists.postValue(Event(true))
    }

    init {
        viewModelScope.launch(dispatcherProvider.io) {
            roomRepository.getPaymentsHistory(null, null, null)
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
    //                          val type = object : TypeToken<List<PaymentHistoryModel>>() {}.type
    //                          var paymentsList: List<PaymentHistoryModel> = gson.fromJson(it.data.data, type)
    //                          _payments.postValue(Event(paymentsList))

                      }
                      is Result.Error -> {

                      }
                      is Result.ErrorResponse -> {

                      }
                  } }
      }
  }
}