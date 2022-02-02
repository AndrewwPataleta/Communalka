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
import com.bumptech.glide.Glide
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Item
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.VersionApp
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.databinding.ItemVersionAppBinding
import com.patstudio.communalka.databinding.ItemYoutubeVideoBinding
import com.skydoves.balloon.*
import gone
import it.sephiroth.android.library.xtooltip.Tooltip
import visible

class YoutubeAdapter(val items: List<Item>, val viewModel: HelpViewModel) : RecyclerView.Adapter<YoutubeAdapter.HistoryVersionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVersionHolder {
        val itemBinding =
            ItemYoutubeVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryVersionHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: HistoryVersionHolder, position: Int) {
        val item: Item = items[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = items.size

    class HistoryVersionHolder(private val itemBinding: ItemYoutubeVideoBinding, val viewModel: HelpViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Item, position: Int) {
            itemBinding.viewModel = viewModel
            itemBinding.position = position
            itemBinding.model = item
            Glide.with(itemBinding.root.context).load(item.snippet.thumbnails.high.url).into(itemBinding.image);
        }
    }
}