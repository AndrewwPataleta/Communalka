package com.patstudio.communalka.presentation.ui.main.room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.data.model.Service
import com.patstudio.communalka.databinding.ItemConnectedPersonalAccountBinding
import com.patstudio.communalka.utils.IconUtils
import getServiceIcon
import gone
import visible

class ConnectedPersonalAccountAdapter(private val accountList: List<Service>, val context: Context, val viewModel: PersonalAccountManagementViewModel) : RecyclerView.Adapter<ConnectedPersonalAccountAdapter.ConnectedPersonalAccountHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectedPersonalAccountHolder {

        val itemBinding =
            ItemConnectedPersonalAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConnectedPersonalAccountHolder(itemBinding,  viewModel)
    }

    override fun onBindViewHolder(holder: ConnectedPersonalAccountHolder, position: Int) {
        val service: Service = accountList[position]
        holder.bind(service, position)
    }

    override fun getItemCount(): Int = accountList.size

    class ConnectedPersonalAccountHolder(private val itemBinding: ItemConnectedPersonalAccountBinding, val viewModel: PersonalAccountManagementViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(service: Service, position: Int) {

            itemBinding.model = service
            itemBinding.account = service.account
            itemBinding.viewModel = viewModel
            itemBinding.position = position
            itemBinding.accountMessage.gone(false)
            IconUtils.instance.getServiceIcon(service.name, itemBinding.root.context,  itemBinding.image)

            service.account?.message?.let {
                if (it.length > 0) {
                    itemBinding.accountMessage.visible(false)
                }
            }
        }
    }
}