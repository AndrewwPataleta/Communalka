package com.patstudio.communalka.presentation.ui.main.room

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.PlacementMeter
import com.patstudio.communalka.databinding.ItemPersonalCounterBinding
import com.patstudio.communalka.presentation.ui.main.readings.TransmissionReadingListViewModel
import com.patstudio.communalka.utils.IconUtils
import getServiceIcon

class PlacementMeterAdapter(private val placementMeters: ArrayList<PlacementMeter>, val context: Context, val viewModel: TransmissionReadingListViewModel) : RecyclerView.Adapter<PlacementMeterAdapter.PlacementMeterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementMeterHolder {

        val itemBinding =
            ItemPersonalCounterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementMeterHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementMeterHolder, position: Int) {
        val placementMeter: PlacementMeter = placementMeters[position]
        holder.bind(placementMeter, position)
    }

    override fun getItemCount(): Int = placementMeters.size

    class PlacementMeterHolder(private val itemBinding: ItemPersonalCounterBinding, val viewModel: TransmissionReadingListViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(placementMeter: PlacementMeter, position: Int) {
            itemBinding.model = placementMeter
            itemBinding.viewModel = viewModel

            Log.d("MeterName", "place ${placementMeter.serviceName}")

            IconUtils.instance.getServiceIcon(placementMeter.serviceName, itemBinding.root.context, itemBinding.imageService)

            if (placementMeter.last_values.lastValue != null) {
               itemBinding.lastValueText.text = "Последние показания"
            } else {
                itemBinding.lastValueText.text = "Введите последние показания"
            }
        }
    }
}