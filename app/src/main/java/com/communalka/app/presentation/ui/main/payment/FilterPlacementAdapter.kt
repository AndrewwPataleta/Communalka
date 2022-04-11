package com.communalka.app.presentation.ui.main.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.data.model.Placement
import com.communalka.app.databinding.ItemFilterPlacementBinding

class FilterPlacementAdapter(private val placementList: List<Placement>, val viewModel: PaymentsViewModel) : RecyclerView.Adapter<FilterPlacementAdapter.PlacementHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        val itemBinding =
            ItemFilterPlacementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val supplier: Placement = placementList[position]
        holder.bind(supplier, position)
    }

    override fun getItemCount(): Int = placementList.size

    class PlacementHolder(private val itemBinding: ItemFilterPlacementBinding,  val viewModel: PaymentsViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(supplier: Placement, position: Int) {
            itemBinding.model = supplier
            itemBinding.checked.setOnCheckedChangeListener { compoundButton, b ->
                viewModel.changeSelectedFilterPlacement(supplier, b)
            }
        }
    }
}