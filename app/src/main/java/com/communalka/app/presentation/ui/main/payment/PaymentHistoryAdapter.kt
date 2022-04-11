package com.communalka.app.presentation.ui.main.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.data.model.PaymentHistoryModel
import com.communalka.app.databinding.ItemPaymentHistoryNewBinding
import roundOffTo2DecPlaces

import java.text.SimpleDateFormat

class PaymentHistoryAdapter(private val paymentsList: List<PaymentHistoryModel>, val viewModel: PaymentsViewModel) : RecyclerView.Adapter<PaymentHistoryAdapter.PlacementHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        val itemBinding =
            ItemPaymentHistoryNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val paymentHistoryModel: PaymentHistoryModel = paymentsList[position]
        holder.bind(paymentHistoryModel, position)
    }

    override fun getItemCount(): Int = paymentsList.size

    class PlacementHolder(private val itemBinding: ItemPaymentHistoryNewBinding,  val viewModel: PaymentsViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(paymentHistoryModel: PaymentHistoryModel, position: Int) {

            val dtStart = paymentHistoryModel.date
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val date = format.parse(dtStart)

            itemBinding.status.text = paymentHistoryModel.status
            itemBinding.date.text = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date)
            itemBinding.placementName.text = paymentHistoryModel.placementName

            if (paymentHistoryModel.receipt != null) {
                itemBinding.receiptNumber.visibility = View.VISIBLE
                itemBinding.receiptNumber.text = "Чек №"+paymentHistoryModel.receipt.number
                itemBinding.receipt.visibility = View.VISIBLE
                itemBinding.receipt.setOnClickListener {
                    viewModel.selectActionReceipt(paymentHistoryModel)
                }
            } else {
                itemBinding.receipt.setOnClickListener {

                }
                itemBinding.receiptNumber.visibility = View.GONE
                itemBinding.receipt.visibility = View.GONE
            }

            itemBinding.paymentAmount.text = "Сумма оплаты: ${roundOffTo2DecPlaces((paymentHistoryModel.amount+paymentHistoryModel.taxAmount).toFloat())} ₽"


        }
    }
}