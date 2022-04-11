package com.communalka.app.presentation.ui.main.room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.communalka.app.data.model.PersonalCounter
import com.communalka.app.databinding.ItemEditPersonalCounterBinding

class EditPersonalCounterAdapter(private val personalCounters: List<PersonalCounter>, val context: Context, val viewModel: EditPersonalAccountViewModel) : RecyclerView.Adapter<EditPersonalCounterAdapter.PersonalCounterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalCounterHolder {

        val itemBinding =
            ItemEditPersonalCounterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonalCounterHolder(itemBinding,  viewModel)
    }

    override fun onBindViewHolder(holder: PersonalCounterHolder, position: Int) {
        val personalCounter: PersonalCounter = personalCounters[position]
        holder.bind(personalCounter, position)
    }

    override fun getItemCount(): Int = personalCounters.size

    class PersonalCounterHolder(private val itemBinding: ItemEditPersonalCounterBinding, val viewModel: EditPersonalAccountViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(personalCounter: PersonalCounter, position: Int) {

            itemBinding.model = personalCounter
            itemBinding.viewModel = viewModel
            itemBinding.position = position

            itemBinding.senderEdit.isEnabled = personalCounter.id == null
        }
    }
}