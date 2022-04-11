package com.communalka.app.presentation.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.communalka.app.data.model.Item
import com.communalka.app.databinding.ItemYoutubeVideoBinding

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