package com.patstudio.communalka.presentation.ui.main.payment

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.PaymentHistoryModel
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.ItemPaymentHistoryBinding
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.presentation.ui.main.profile.welcome.WelcomeViewModel
import com.skydoves.balloon.*
import gone
import visible

class PaymentHistoryAdapter(private val paymentsList: List<PaymentHistoryModel>, val viewModel: PaymentsViewModel) : RecyclerView.Adapter<PaymentHistoryAdapter.PlacementHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        val itemBinding =
            ItemPaymentHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val paymentHistoryModel: PaymentHistoryModel = paymentsList[position]
        holder.bind(paymentHistoryModel, position)
    }

    override fun getItemCount(): Int = paymentsList.size

    class PlacementHolder(private val itemBinding: ItemPaymentHistoryBinding,  val viewModel: PaymentsViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(paymentHistoryModel: PaymentHistoryModel, position: Int) {

        }
    }
}