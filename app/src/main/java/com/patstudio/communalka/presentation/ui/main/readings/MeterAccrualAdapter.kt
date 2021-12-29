package com.patstudio.communalka.presentation.ui.main.readings

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.ConsumptionHistory
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.PlacementMeter
import com.patstudio.communalka.data.model.VersionApp
import com.patstudio.communalka.databinding.ItemConsumptionHistoryBinding
import com.patstudio.communalka.databinding.ItemMeterAccrualBinding
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.databinding.ItemVersionAppBinding
import com.skydoves.balloon.*
import gone
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.NonCancellable.children
import visible
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