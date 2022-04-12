package com.communalka.app.presentation.ui.main.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.communalka.app.BuildConfig
import com.communalka.app.R
import com.communalka.app.common.utils.roundOffTo2DecPlaces
import com.communalka.app.data.model.Placement
import com.communalka.app.databinding.*
import com.communalka.app.presentation.ui.main.readings.PlacementSelectorPaymentAdapter
import org.koin.android.viewmodel.ext.android.sharedViewModel

import ru.tinkoff.acquiring.sdk.TinkoffAcquiring
import ru.tinkoff.acquiring.sdk.models.Item
import ru.tinkoff.acquiring.sdk.models.Receipt
import ru.tinkoff.acquiring.sdk.models.enums.CheckType
import ru.tinkoff.acquiring.sdk.models.enums.Tax
import ru.tinkoff.acquiring.sdk.models.enums.Taxation
import ru.tinkoff.acquiring.sdk.models.options.screen.PaymentOptions
import ru.tinkoff.acquiring.sdk.utils.Money
import java.util.ArrayList

class PaymentPlacementFragment : Fragment() {

    private var _binding: FragmentPaymentPlacementBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<PaymentPlacementViewModel>()
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

        viewModel.rebuildPosition.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    paymentsAdapter.notifyItemChanged(it)
                }
            }
        }
        viewModel.placementsList.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val adapter = PlacementSelectorPaymentAdapter(requireContext(), it, viewModel)
                    binding.placementSelector.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                viewModel.selectedPlacement(parent.getItemAtPosition(position) as Placement)
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    binding.placementSelector.adapter = adapter
                }
            }
        }
        viewModel.totalPrice.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   if (it > 0) {
                       binding.payment.text = "Оплатить: ${roundOffTo2DecPlaces(it.toFloat())} ₽"
                       binding.payment.setOnClickListener {
                           viewModel.createPayment()
                       }
                       binding.payment.background = requireContext().resources.getDrawable(R.drawable.background_rounded_blue)
                   } else {
                       binding.payment.setOnClickListener {}
                       binding.payment.background = requireContext().resources.getDrawable(R.drawable.gray_button_disable_background)
                       binding.payment.text = "Оплатить"
                   }
                }
            }
        }

        viewModel.paymentOrder.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    var receiptOrder = Receipt().apply {
                        taxation = Taxation.USN_INCOME_OUTCOME
                        items = it.services?.map { service ->
                            Item().apply {
                                name = service.second
                                quantity = 1.0
                                amount = service.first
                                price = service.first 
                                tax = Tax.NONE
                            }} as ArrayList<Item>
                        email = it.email
                    }
                    val paymentOptions =
                    PaymentOptions().setOptions {
                        orderOptions {
                            orderId = it.orderNumber.toString()
                            amount = Money.ofRubles(it.amount)
                            title = "Оплата коммунальных услуг"
                            //receipt = receiptOrder
                            recurrentPayment = false
                            shops = it.shops
                        }
                        customerOptions {
                            customerKey = "1"
                            email = it.email
                            checkType = CheckType.THREE_DS.toString()
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
        if (requestCode == 123) {

            if (resultCode == Activity.RESULT_OK) {
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setMessage( "Ваша оплата проведена успешно! Мы сообщим вам, когда будут загружены чеки!")
                builder.setPositiveButton("Ok"){dialogInterface, which ->
                    dialogInterface.dismiss()
                    findNavController().navigate(R.id.toPayments)
                }
                builder.setCancelable(true)
                builder.show()
            }
        }

    }

    private fun initListeners() {
        binding.payment.setOnClickListener {
            viewModel.createPayment()

//
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelableArrayList<Placement>("placements")?.let {
            viewModel.setCurrentPlacements(it)
        }
        initObservers()
        initListeners()
        viewModel.initCurrentUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}