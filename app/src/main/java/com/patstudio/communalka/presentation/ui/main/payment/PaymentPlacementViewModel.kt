package com.patstudio.communalka.presentation.ui.main.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.PaymentHistoryModel
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository

class PaymentPlacementViewModel(private val userRepository: UserRepository, private val gson: Gson, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private var _payments: MutableLiveData<Event<List<PaymentHistoryModel>>> = MutableLiveData()
    val payments: LiveData<Event<List<PaymentHistoryModel>>> = _payments

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    private var _placement: MutableLiveData<Event<Placement>> = MutableLiveData()
    val placement: LiveData<Event<Placement>> = _placement

    fun setCurrentPlacement(placement: Placement) {
        _placement.postValue(Event(placement))
    }
}