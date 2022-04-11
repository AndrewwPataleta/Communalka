package com.communalka.app.presentation.ui.main.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.data.model.Supplier
import com.communalka.app.databinding.ItemFilterSupplierBinding

class FilterSupplierAdapter(private val supplierList: List<Supplier>, val viewModel: PaymentsViewModel) : RecyclerView.Adapter<FilterSupplierAdapter.PlacementHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        val itemBinding =
            ItemFilterSupplierBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val supplier: Supplier = supplierList[position]
        holder.bind(supplier, position)
    }

    override fun getItemCount(): Int = supplierList.size

    class PlacementHolder(private val itemBinding: ItemFilterSupplierBinding,  val viewModel: PaymentsViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(supplier: Supplier, position: Int) {
            itemBinding.model = supplier
            itemBinding.checked.setOnCheckedChangeListener { compoundButton, b ->
                viewModel.changeSelectedFilterSupplier(supplier, b)
            }
        }
    }
}