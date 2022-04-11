package com.communalka.app.presentation.ui.main.readings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.data.model.PlacementMeter
import com.communalka.app.databinding.ItemMeterAccrualBinding
import java.text.SimpleDateFormat

class MeterAccrualAdapter(val placementList: List<PlacementMeter>, val viewModel: AccrualViewModel) : RecyclerView.Adapter<MeterAccrualAdapter.HistoryVersionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVersionHolder {
        val itemBinding =
            ItemMeterAccrualBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryVersionHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: HistoryVersionHolder, position: Int) {
        val consumptionHistory = placementList[position]
        holder.bind(consumptionHistory, position)
    }

    override fun getItemCount(): Int = placementList.size

    class HistoryVersionHolder(private val itemBinding: ItemMeterAccrualBinding, val viewModel: AccrualViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(meter: PlacementMeter, position: Int) {

                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val date = format.parse(meter.last_values.datePrevValue)

                itemBinding.lastDateValue.text = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date)
                itemBinding.model = meter
                itemBinding.position = position
                itemBinding.viewModel = viewModel
                meter.serial_number?.let {
                    itemBinding.serial.text = "№ ${it}"
                }

            }
    }
}