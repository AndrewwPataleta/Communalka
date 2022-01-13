package com.patstudio.communalka.presentation.ui.main.payment

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
import ru.tinkoff.acquiring.sdk.models.Shop
import ru.tinkoff.acquiring.sdk.utils.Money
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong

class PaymentPlacementViewModel(private val userRepository: UserRepository, private val dispatcherProvider: DispatcherProvider, private val gson: Gson, private val roomRepository: RoomRepository): ViewModel() {

    private lateinit var placementModel: Placement
    private var placements: ArrayList<Placement>? = null
    private lateinit var user: User

    private var _payments: MutableLiveData<Event<List<PaymentHistoryModel>>> = MutableLiveData()
    val payments: LiveData<Event<List<PaymentHistoryModel>>> = _payments

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    private var _placementsList: MutableLiveData<Event<ArrayList<Placement>>> = MutableLiveData()
    val placementsList: LiveData<Event<ArrayList<Placement>>> = _placementsList

    private var _showProgressPayment: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgressPayment: LiveData<Event<Boolean>> = _showProgressPayment

    private var _paymentOrder: MutableLiveData<Event<PaymentOrderShop>> = MutableLiveData()
    val paymentOrder: LiveData<Event<PaymentOrderShop>> = _paymentOrder

    private var _placement: MutableLiveData<Event<Placement>> = MutableLiveData()
    val placement: LiveData<Event<Placement>> = _placement

    private var _totalPrice: MutableLiveData<Event<Double>> = MutableLiveData()
    val totalPrice: LiveData<Event<Double>> = _totalPrice

    private var _rebuildPosition: MutableLiveData<Event<Int>> = MutableLiveData()
    val rebuildPosition: LiveData<Event<Int>> = _rebuildPosition

    fun setCurrentPlacements(placements: ArrayList<Placement>) {
        var indexSelected = 0
        this.placements = placements
        placements.forEachIndexed { index, it ->
            it.invoices?.map {
                it.selected = true
            }
            if (it.selected) {
                this.placementModel = it
                indexSelected = index
                Collections.swap(placements, 0, indexSelected);
                _placementsList.postValue(Event(placements))

            }
        }
    }

    private fun getPlacementsWithInvoices() {
            viewModelScope.launch(dispatcherProvider.io) {
                roomRepository.getUserPremises()
                    .catch {
                        it.printStackTrace()
                    }
                    .collect {
                        when (it) {
                            is Result.Success -> {
                                var placementList: PlacementWrapper = gson.fromJson(it.data.data, PlacementWrapper::class.java)
                                if (placementList.placements.count() > 0) {
                                    var placementLocal = roomRepository.getUserPremises(user!!.id)
                                    placements = placementList.placements
                                    placementList.placements.map { parent ->
                                        placementLocal.map { child ->
                                            if (parent.id.compareTo(child.id) == 0) {
                                                parent.imageType = child.imageType
                                                parent.path = child.imagePath
                                            }
                                        }
                                    }
                                    updateInvoicesForPlacement(placementList.placements, 0)
                                }
                            }
                            is Result.ErrorResponse -> { }
                            is Result.Error -> { }
                        }
                    }
            }
    }

    private suspend fun updateInvoicesForPlacement(placements: ArrayList<Placement>, pos: Int) {
        if (pos <= placements.size-1) {
            var placement = placements.get(pos)
            roomRepository.getPlacementInvoice(placement)
                .collect {
                    when (it) {
                        is Result.Success -> {
                            val turnsType = object : TypeToken<ArrayList<Invoice>>() {}.type
                            var invoices: ArrayList<Invoice> = gson.fromJson(it.data.data, turnsType)
                            placement.invoices = invoices
                            updateInvoicesForPlacement(placements, pos+1)
                        }
                    }
                }
        } else {
            placements.forEachIndexed { index, it ->
                it.invoices?.map {
                    it.selected = true
                }
                if (index == 0) {
                    this.placementModel = it
                    _placementsList.postValue(Event(placements))
                }
            }
        }
    }

    fun initCurrentUser() {
        viewModelScope.launch(dispatcherProvider.io) {
            user = userRepository.getLastAuthUser()

            if (placements.isNullOrEmpty()) {

                getPlacementsWithInvoices()
            }
        }
    }

    fun selectedPlacement(placement: Placement) {
        placements?.map {
            it.selected = it.id.compareTo(placement.id) == 0
        }
        placementModel = placement
        _placement.postValue(Event(placementModel))
        calculatePrice()

    }

    private fun calculatePrice(){
        var paymentAmount = 0.0
       placementModel.invoices?.map {
           if (it.selected) {
               var amount = it.penalty+it.balance
               it.penaltyValue?.let {
                   amount = it
               }
               val roundOff = Math.round((amount+((amount*it.percentTax)/100)) * 100) / 100.0
               paymentAmount += roundOff
           }
       }

        _totalPrice.postValue(Event(paymentAmount))
    }

    fun setPenaltyValue(invoice: Invoice, penalty: String, position: Int) {
        invoice.penaltyValue = null
        if (penalty.toDoubleOrNull() != null) {
            invoice.penaltyValue = penalty.toDouble()
        } else {
            invoice.penaltyValue = 0.0
        }

        calculatePrice()
    }


    fun changeSelectTypeInvoice(invoice: Invoice, selected: Boolean) {
        invoice.selected = selected
        calculatePrice()
    }

    fun createPayment() {

        _showProgressPayment.postValue(Event(true))
        var paymentCreatorList: ArrayList<PaymentCreator> = ArrayList()
        var totalAmount = 0.0
        var totalTax = 0.0
        placementModel.accounts.map { account->
            placementModel.invoices?.map { invoice->
               if (invoice.selected) {
                   if (account.supplierName.compareTo(invoice.supplier) == 0) {
                       var amount = invoice.penalty+invoice.balance
                       invoice.penaltyValue?.let {
                           amount = it
                       }

                        var tax = ((amount*invoice.percentTax)/100)

                        var paymentCreator = PaymentCreator(account = account.id, amount = amount, taxAmount = tax, shopId = invoice.shopId)

                       paymentCreatorList.add(paymentCreator)
                   }
               }
            }
        }

        paymentCreatorList.map {
            totalAmount += it.amount
            totalTax += it.taxAmount
        }

        var order = OrderCreator(totalAmount, totalTax, paymentCreatorList)
        viewModelScope.launch {
            userRepository.createOrder(order)
                .collect {
                    when (it) {
                        is Result.Success -> {
                            val paymentOrderShop: PaymentOrderShop = gson.fromJson(it.data.data, PaymentOrderShop::class.java)
                            paymentOrderShop.amount = totalAmount+totalTax

                            val shops: ArrayList<Shop> = ArrayList()
                            paymentCreatorList.map {
                                val shop = Shop()
                                val taxShop = it.amount

                                shop.amount =  Money.ofRubles(taxShop).coins
                                shop.shopCode = it.shopId

                                shops.add(shop)
                            }
                            val shop = Shop()
                            shop.amount =  Money.ofRubles(totalTax).coins
                            shop.shopCode = paymentOrderShop.communalkaShopId.toString()
                            shops.add(shop)

                            paymentOrderShop.shops = shops

                            _paymentOrder.postValue(Event(paymentOrderShop))
                        }
                    }
                }
        }
    }

}