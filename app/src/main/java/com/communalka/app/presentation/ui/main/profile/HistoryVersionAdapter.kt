package com.communalka.app.presentation.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.common.utils.gone
import com.communalka.app.common.utils.visible
import com.communalka.app.data.model.VersionApp
import com.communalka.app.databinding.ItemVersionAppBinding
import com.skydoves.balloon.*

class HistoryVersionAdapter(val placementList: List<VersionApp>, val viewModel: HistoryVersionViewModel) : RecyclerView.Adapter<HistoryVersionAdapter.HistoryVersionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVersionHolder {
        val itemBinding =
            ItemVersionAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryVersionHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: HistoryVersionHolder, position: Int) {
        val versionApp: VersionApp = placementList[position]
        holder.bind(versionApp, position)
    }

    override fun getItemCount(): Int = placementList.size

    class HistoryVersionHolder(private val itemBinding: ItemVersionAppBinding, val viewModel: HistoryVersionViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(versionApp: VersionApp, position: Int) {
            itemBinding.viewModel = viewModel
            itemBinding.position = position
            if (!versionApp.opened) {
                itemBinding.descriptionVersion.visible(false)
                itemBinding.arrow.rotation = 0f
                itemBinding.descriptionVersion.gone(false)
            } else {
                itemBinding.descriptionVersion.gone(false)
                itemBinding.arrow.rotation = 180f
                itemBinding.descriptionVersion.visible(false)
            }
        }
    }
}