package com.patstudio.communalka.presentation.ui.main.payment

import android.Manifest
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
import ru.tinkoff.acquiring.sdk.TinkoffAcquiring
import ru.tinkoff.acquiring.sdk.models.enums.CheckType
import ru.tinkoff.acquiring.sdk.models.options.screen.PaymentOptions
import ru.tinkoff.acquiring.sdk.utils.Money
import visible

class PaymentPlacementFragment : Fragment() {

    private var _binding: FragmentPaymentPlacementBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PaymentPlacementViewModel>()
    private lateinit var paymentsAdapter: PaymentHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPaymentPlacementBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun initObservers() {
        viewModel.placement.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    this.binding.model = it
                }
            }
        }
    }

    private fun initListeners() {
        binding.payment.setOnClickListener {
            val paymentOptions =
                PaymentOptions().setOptions {
                    orderOptions {
                        orderId = "1234"
                        amount = Money.ofRubles(1000)
                        title = "НАЗВАНИЕ ПЛАТЕЖА"
                        description = "ОПИСАНИЕ ПЛАТЕЖА"
                        recurrentPayment = false
                    }
                    customerOptions {
                        checkType = CheckType.NO.toString()
                        customerKey = "CUSTOMER_KEY"
                        email = "batman@gotham.co"
                    }

                }

            val tinkoffAcquiring = TinkoffAcquiring("", "")
            tinkoffAcquiring.openPaymentScreen(this, paymentOptions, 123)
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