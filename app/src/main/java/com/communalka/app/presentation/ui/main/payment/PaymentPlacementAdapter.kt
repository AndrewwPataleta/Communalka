package com.communalka.app.presentation.ui.main.payment

import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.R
import com.communalka.app.common.utils.DecimalDigitsInputFilter
import com.communalka.app.common.utils.InputFilterMinMax
import com.communalka.app.common.utils.roundOffTo2DecPlaces
import com.communalka.app.data.model.Invoice
import com.communalka.app.databinding.ItemPlacementPaymentBinding


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