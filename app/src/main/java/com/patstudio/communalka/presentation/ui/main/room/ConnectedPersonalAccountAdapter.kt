package com.patstudio.communalka.presentation.ui.main.room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.data.model.PersonalAccount
import com.patstudio.communalka.databinding.ItemConnectedPersonalAccountBinding
import gone
import visible

class ConnectedPersonalAccountAdapter(private val accountList: List<PersonalAccount>, val context: Context, val viewModel: PersonalAccountManagementViewModel) : RecyclerView.Adapter<ConnectedPersonalAccountAdapter.ConnectedPersonalAccountHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectedPersonalAccountHolder {

        val itemBinding =
            ItemConnectedPersonalAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConnectedPersonalAccountHolder(itemBinding,  viewModel)
    }

    override fun onBindViewHolder(holder: ConnectedPersonalAccountHolder, position: Int) {
        val personalAccount: PersonalAccount = accountList[position]
        holder.bind(personalAccount, position)
    }

    override fun getItemCount(): Int = accountList.size

    class ConnectedPersonalAccountHolder(private val itemBinding: ItemConnectedPersonalAccountBinding, val viewModel: PersonalAccountManagementViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(personalAccount: PersonalAccount, position: Int) {

            itemBinding.model = personalAccount
            itemBinding.account = personalAccount.account
            itemBinding.viewModel = viewModel
            itemBinding.position = position
//            if (personalAccount.account.message.length > 0 )
//                itemBinding.accountMessage.visible(false)
//            else
//                itemBinding.accountMessage.gone(false)
        }
    }
}