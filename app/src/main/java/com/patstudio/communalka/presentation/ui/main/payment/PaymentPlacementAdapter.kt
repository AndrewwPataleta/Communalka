package com.patstudio.communalka.presentation.ui.main.payment

import android.content.Context
import android.content.res.Resources
import android.text.InputFilter
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
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
import round
import roundOffTo2DecPlaces
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
            itemBinding.selectedPayment.isChecked = invoice.selected

            itemBinding.balanceValue.text  = roundOffTo2DecPlaces(invoice.balance.toFloat())
            itemBinding.penaltyValue.text  = roundOffTo2DecPlaces(invoice.penalty.toFloat())


            itemBinding.selectedPayment.setOnCheckedChangeListener { compoundButton, b ->
                viewModel.changeSelectTypeInvoice(invoice, b)

                if (b) {
                    itemBinding.openGroup.visibility = View.VISIBLE

                    itemBinding.service.setTextColor(itemBinding.root.context.resources.getColor(R.color.black))
                } else {
                    itemBinding.openGroup.visibility = View.GONE
                    itemBinding.service.setTextColor(itemBinding.root.context.resources.getColor(R.color.gray_dark))
                }

                if (invoice.penalty < 1) {
                    itemBinding.penaltyGroup.visibility = View.GONE
                } else {
                    itemBinding.penaltyGroup.visibility = View.VISIBLE
                }
            }
            var amount = invoice.penalty+invoice.balance
            invoice.penaltyValue?.let {
                amount = it
            }

            var tax = ((amount*invoice.percentTax)/100)
            itemBinding.penaltyPercent.text = "Комиссия ${roundOffTo2DecPlaces(tax.toFloat())} ₽"
            itemBinding.penaltyPaymentValue.setFilters(arrayOf<InputFilter>(InputFilterMinMax(0f, 1000000f), DecimalDigitsInputFilter(10, 2)))
            itemBinding.penaltyPaymentValue.setText(amount.toString()   )
            itemBinding.penaltyPaymentValue.addTextChangedListener {
                if (it.toString().toDoubleOrNull() != null) {
                    var amount = it.toString().toDoubleOrNull()
                    var tax = ((amount!!*invoice.percentTax)/100)
                    itemBinding.penaltyPercent.text = "Комиссия ${roundOffTo2DecPlaces(tax.toFloat())} ₽"
                } else {

                    itemBinding.penaltyPercent.text = "Размер комиссии появится после ввода суммы"
                }
                viewModel.setPenaltyValue(invoice, it.toString(), position)
            }

            if (invoice.penalty < 1) {
                itemBinding.penaltyGroup.visibility = View.GONE
            } else {
                itemBinding.penaltyGroup.visibility = View.VISIBLE
            }

        }
    }
}