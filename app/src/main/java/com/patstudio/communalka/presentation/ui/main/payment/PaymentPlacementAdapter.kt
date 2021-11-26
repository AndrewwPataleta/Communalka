package com.patstudio.communalka.presentation.ui.main.payment

import android.content.Context
import android.content.res.Resources
import android.text.InputFilter
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.common.utils.DecimalDigitsInputFilter
import com.patstudio.communalka.common.utils.InputFilterMinMax
import com.patstudio.communalka.data.model.Invoice
import com.patstudio.communalka.data.model.PaymentHistoryModel
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.ItemPaymentHistoryBinding
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.databinding.ItemPlacementPaymentBinding
import com.patstudio.communalka.presentation.ui.main.profile.welcome.WelcomeViewModel
import com.skydoves.balloon.*
import gone
import visible

class PaymentPlacementAdapter(private val paymentsList: List<Invoice>, val viewModel: PaymentPlacementViewModel) : RecyclerView.Adapter<PaymentPlacementAdapter.PlacementHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        val itemBinding =
            ItemPlacementPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val invoice: Invoice = paymentsList[position]
        holder.bind(invoice, position)
    }

    override fun getItemCount(): Int = paymentsList.size

    class PlacementHolder(private val itemBinding: ItemPlacementPaymentBinding,  val viewModel: PaymentPlacementViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(invoice: Invoice, position: Int) {
            itemBinding.model = invoice
            itemBinding.viewModel = viewModel
            itemBinding.selectedPayment.setOnCheckedChangeListener { compoundButton, b ->
                viewModel.changeSelectTypeInvoice(invoice, b)
            }
            itemBinding.penaltyPaymentValue.setFilters(arrayOf<InputFilter>(InputFilterMinMax(0f, invoice.penalty.toFloat()), DecimalDigitsInputFilter(10, 2)))

            itemBinding.penaltyPaymentValue.addTextChangedListener {
                viewModel.setPenaltyValue(invoice, it.toString())
            }
        }
    }
}