package com.patstudio.communalka.presentation.ui.main.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.contextprovider.DispatcherProvider
import com.google.gson.Gson
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.*
import com.patstudio.communalka.data.repository.premises.RoomRepository
import com.patstudio.communalka.data.repository.user.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.tinkoff.acquiring.sdk.models.Shop
import ru.tinkoff.acquiring.sdk.utils.Money
import kotlin.math.roundToLong

class PaymentPlacementViewModel(private val userRepository: UserRepository, private val gson: Gson, private val roomRepository: RoomRepository, private val dispatcherProvider: DispatcherProvider): ViewModel() {

    private lateinit var placementModel: Placement

    private var _payments: MutableLiveData<Event<List<PaymentHistoryModel>>> = MutableLiveData()
    val payments: LiveData<Event<List<PaymentHistoryModel>>> = _payments

    private var _showProgress: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgress: LiveData<Event<Boolean>> = _showProgress

    private var _showProgressPayment: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showProgressPayment: LiveData<Event<Boolean>> = _showProgressPayment

    private var _paymentOrder: MutableLiveData<Event<PaymentOrderShop>> = MutableLiveData()
    val paymentOrder: LiveData<Event<PaymentOrderShop>> = _paymentOrder

    private var _placement: MutableLiveData<Event<Placement>> = MutableLiveData()
    val placement: LiveData<Event<Placement>> = _placement

    private var _totalPrice: MutableLiveData<Event<Double>> = MutableLiveData()
    val totalPrice: LiveData<Event<Double>> = _totalPrice

    fun setCurrentPlacement(placement: Placement) {
        this.placementModel = placement
        _placement.postValue(Event(placement))
    }


    private fun calculatePrice(){
        var paymentAmount = 0.0
       placementModel.invoices?.map {
           if (it.selected) {
               var amount = it.penalty
               it.penaltyValue?.let {
                   amount = it
               }
               val roundOff = Math.round((amount+((amount*it.percentTax)/100)) * 100) / 100.0
               paymentAmount += roundOff
           }
       }

        _totalPrice.postValue(Event(paymentAmount))
    }

    fun setPenaltyValue(invoice: Invoice, penalty: String) {
        invoice.penaltyValue = null
       penalty.toDoubleOrNull()?.let {
           invoice.penaltyValue = it
       }
        calculatePrice()
    }


    fun changeSelectTypeInvoice(invoice: Invoice, selected: Boolean) {
        invoice.selected = selected
        calculatePrice()
    }

    public fun createPayment() {

        _showProgressPayment.postValue(Event(true))
        var paymentCreatorList: ArrayList<PaymentCreator> = ArrayList()
        var totalAmount = 0.0
        var totalTax = 0.0
        placementModel.accounts.map { account->
            placementModel.invoices?.map { invoice->
               if (invoice.selected) {
                   if (account.supplierName.compareTo(invoice.supplier) == 0) {
                       var amount = invoice.penalty
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
                            var paymentOrderShop: PaymentOrderShop = gson.fromJson(it.data.data, PaymentOrderShop::class.java)
                            paymentOrderShop.amount = totalAmount+totalTax

                            var shops: ArrayList<Shop> = ArrayList()
                            paymentCreatorList.map {
                                var shop = Shop()
                                var taxShop = it.amount+it.taxAmount

                                shop.amount =  Money.ofRubles(taxShop).coins
                                shop.shopCode = paymentOrderShop.communalkaShopId.toString()
                                shops.add(shop)
                            }
                            paymentOrderShop.shops = shops

                            _paymentOrder.postValue(Event(paymentOrderShop))
                        }
                        is Result.Error -> {
                            _showProgressPayment.postValue(Event(false))
                        }
                        is Result.ErrorResponse -> {
                            _showProgressPayment.postValue(Event(false))
                        }
                    }
                }
        }
    }

}