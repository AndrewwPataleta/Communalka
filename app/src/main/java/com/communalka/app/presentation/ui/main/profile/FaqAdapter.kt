package com.communalka.app.presentation.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.data.model.Faq
import com.communalka.app.databinding.ItemFaqBinding
import gone
import visible

class FaqAdapter(val faq: List<Faq>, val viewModel: FaqViewModel) : RecyclerView.Adapter<FaqAdapter.HistoryVersionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVersionHolder {
        val itemBinding =
            ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryVersionHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: HistoryVersionHolder, position: Int) {
        val versionApp: Faq = faq[position]
        holder.bind(versionApp, position)
    }

    override fun getItemCount(): Int = faq.size

    class HistoryVersionHolder(private val itemBinding: ItemFaqBinding, val viewModel: FaqViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(versionApp: Faq, position: Int) {
            itemBinding.model = versionApp
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