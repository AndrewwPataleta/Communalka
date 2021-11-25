package com.patstudio.communalka.presentation.ui.main.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.data.model.Service
import com.patstudio.communalka.databinding.ItemFilterServiceBinding

class FilterServiceAdapter(private val servicesList: List<Service>, val viewModel: PaymentsViewModel) : RecyclerView.Adapter<FilterServiceAdapter.PlacementHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        val itemBinding =
            ItemFilterServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val supplier: Service = servicesList[position]
        holder.bind(supplier, position)
    }

    override fun getItemCount(): Int = servicesList.size

    class PlacementHolder(private val itemBinding: ItemFilterServiceBinding,  val viewModel: PaymentsViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(supplier: Service, position: Int) {
            itemBinding.model = supplier
            itemBinding.checked.setOnCheckedChangeListener { compoundButton, b ->
                viewModel.changeSelectedFilterService(supplier, b)
            }
        }
    }
}