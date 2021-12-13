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
import com.patstudio.communalka.data.model.VersionApp
import com.patstudio.communalka.databinding.ItemConsumptionHistoryBinding
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.databinding.ItemVersionAppBinding
import com.skydoves.balloon.*
import gone
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.NonCancellable.children
import visible

class ConsumptionHistoryAdapter(val placementList: List<ConsumptionHistory>, val viewModel: ConsumptionHistoryViewModel) : RecyclerView.Adapter<ConsumptionHistoryAdapter.HistoryVersionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVersionHolder {
        val itemBinding =
            ItemConsumptionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryVersionHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: HistoryVersionHolder, position: Int) {
        val consumptionHistory = placementList[position]
        holder.bind(consumptionHistory, position)
    }

    override fun getItemCount(): Int = placementList.size

    class HistoryVersionHolder(private val itemBinding: ItemConsumptionHistoryBinding, val viewModel: ConsumptionHistoryViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(consumptionHistory: ConsumptionHistory, position: Int) {
            itemBinding.informationText.text = "История потребления за ${consumptionHistory.period_string}"
            itemBinding.table.removeAllViews()
            val inflater = itemBinding.root.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowHeader: View = inflater.inflate(R.layout.item_consumption_history_header, null)
            var tableRow = TableRow(itemBinding.root.context)
            tableRow.addView(rowHeader)
            itemBinding.table.addView(tableRow)
            consumptionHistory.children.map { parent->
                var tableRow = TableRow(itemBinding.root.context)


                val rowParent: View = inflater.inflate(R.layout.layout_meter_history_parent, null)
                rowParent.findViewById<TextView>(R.id.month).text = parent.period_string
                rowParent.findViewById<TextView>(R.id.testimony).text = parent.value.toString()
                rowParent.findViewById<TextView>(R.id.testimony).text = parent.value.toString()
                rowParent.findViewById<TextView>(R.id.consumption).text = parent.consumption.toString()

                tableRow.addView(rowParent)

                rowParent.findViewById<ImageView>(R.id.open).setOnClickListener {
                    viewModel.updateOpenedTable(parent, position)
                }

                itemBinding.table.addView(tableRow)

                if (parent.isOpened) {

                    rowParent.findViewById<ImageView>(R.id.open).rotation = 90f
                    parent.children.map { child ->
                        var tableRowChild = TableRow(itemBinding.root.context)
                        val rowChild: View =
                            inflater.inflate(R.layout.layout_meter_history_child, null)
                        rowChild.findViewById<TextView>(R.id.date).text = child.period_string
                        rowChild.findViewById<TextView>(R.id.testimony).text =
                            child.value.toString()
                        rowChild.findViewById<TextView>(R.id.consumption).text =
                            child.consumption.toString()
                        tableRowChild.addView(rowChild)
                        itemBinding.table.addView(tableRowChild)
                    }
                } else {
                    rowParent.findViewById<ImageView>(R.id.open).rotation = 270f
                }
            }
        }
    }
}