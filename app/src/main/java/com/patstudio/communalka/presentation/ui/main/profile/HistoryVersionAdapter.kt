package com.patstudio.communalka.presentation.ui.main.profile

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.VersionApp
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.databinding.ItemVersionAppBinding
import com.skydoves.balloon.*
import gone
import it.sephiroth.android.library.xtooltip.Tooltip
import visible

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