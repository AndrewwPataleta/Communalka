package com.patstudio.communalka.presentation.ui.main.payment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.*
import com.patstudio.communalka.presentation.ui.MainActivity
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import com.patstudio.communalka.presentation.ui.main.profile.HistoryVersionViewModel
import com.patstudio.communalka.presentation.ui.main.room.PlacementAdapter
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import roundOffTo2DecPlaces
import ru.tinkoff.acquiring.sdk.TinkoffAcquiring
import ru.tinkoff.acquiring.sdk.models.Shop
import ru.tinkoff.acquiring.sdk.models.enums.CheckType
import ru.tinkoff.acquiring.sdk.models.options.screen.PaymentOptions
import ru.tinkoff.acquiring.sdk.utils.Money
import visible

class PaymentPlacementFragment : Fragment() {

    private var _binding: FragmentPaymentPlacementBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PaymentPlacementViewModel>()
    private lateinit var paymentsAdapter: PaymentPlacementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPaymentPlacementBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        viewModel.placement.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    this.binding.model = it
                    paymentsAdapter = PaymentPlacementAdapter(it.invoices!!, viewModel)
                    binding.paymentPlacementContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.paymentPlacementContainer.adapter = paymentsAdapter
                }
            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   if (it > 0) {
                       binding.payment.isEnabled = true
                       binding.payment.text = "Оплатить: ${roundOffTo2DecPlaces(it.toFloat())} ₽"

                   } else {
                       binding.payment.text = "Оплатить"
                       binding.payment.isEnabled = false
                   }
                }
            }
        }

        viewModel.paymentOrder.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val paymentOptions =
                    PaymentOptions().setOptions {
                        orderOptions {

                            Log.d("PaymentPlacement", "money ${Money.ofRubles(it.amount)}")
                            Log.d("PaymentPlacement", "amount ${it.amount}")

                            orderId = it.orderNumber.toString()
                            amount = Money.ofRubles(it.amount)
                            title = "Оплата коммунальных услуг"
                            recurrentPayment = false
                            shops = it.shops
                        }
                        customerOptions {
                            checkType = CheckType.NO.toString()
                        }
                    }

                    val tinkoffAcquiring = TinkoffAcquiring(BuildConfig.TERMINAL_KEY, BuildConfig.PUBLIC_KEY)
                    tinkoffAcquiring.openPaymentScreen(this, paymentOptions, 123)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("Result", data.toString())
    }

    private fun initListeners() {
        binding.payment.setOnClickListener {
            viewModel.createPayment()

//
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Placement>("placement")?.let {
            viewModel.setCurrentPlacement(it)
        }
        initObservers()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}