package com.patstudio.communalka.presentation.ui.main.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.ItemPlacementBinding

class PlacementAdapter(private val placementList: List<Placement>) : RecyclerView.Adapter<PlacementAdapter.PlacementHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        val itemBinding =
            ItemPlacementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val placement: Placement = placementList[position]
        holder.bind(placement)
    }

    override fun getItemCount(): Int = placementList.size

    class PlacementHolder(private val itemBinding: ItemPlacementBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(placement: Placement) {
            itemBinding.placementName.text = placement.name

        }
    }
}