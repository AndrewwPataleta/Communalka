package com.communalka.app.presentation.ui.main.room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.data.model.Service
import com.communalka.app.databinding.ItemUnconnectedPersonalAccountBinding
import com.communalka.app.utils.IconUtils

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
            IconUtils.instance.getServiceIcon(service.name, itemBinding.root.context, itemBinding.image)
            itemBinding.position = position

        }
    }
}