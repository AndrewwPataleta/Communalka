package com.patstudio.communalka.presentation.ui.main.room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.data.model.Service
import com.patstudio.communalka.databinding.ItemUnconnectedPersonalAccountBinding

class UnconnectedPersonalAccountAdapter(private val accountList: List<Service>, val context: Context, val viewModel: PersonalAccountManagementViewModel) : RecyclerView.Adapter<UnconnectedPersonalAccountAdapter.UnconnectedPersonalAccountHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnconnectedPersonalAccountHolder {

        val itemBinding =
            ItemUnconnectedPersonalAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UnconnectedPersonalAccountHolder(itemBinding,  viewModel)
    }

    override fun onBindViewHolder(holder: UnconnectedPersonalAccountHolder, position: Int) {
        val service: Service = accountList[position]
        holder.bind(service, position)
    }

    override fun getItemCount(): Int = accountList.size

    class UnconnectedPersonalAccountHolder(private val itemBinding: ItemUnconnectedPersonalAccountBinding, val viewModel: PersonalAccountManagementViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(service: Service, position: Int) {

            itemBinding.model = service
            itemBinding.viewModel = viewModel
            itemBinding.position = position

        }
    }
}