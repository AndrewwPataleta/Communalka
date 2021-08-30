package com.patstudio.communalka.presentation.ui.main.profile

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.model.VersionApp
import com.patstudio.communalka.databinding.ItemSwitchUserBinding
import com.patstudio.communalka.databinding.ItemVersionAppBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import com.skydoves.balloon.extensions.dp
import gone
import visible

class SwitchProfileAdapter(val placementList: List<User>, val viewModel: ProfileViewModel) : RecyclerView.Adapter<SwitchProfileAdapter.SwitchProfileHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwitchProfileHolder {
        val itemBinding =
            ItemSwitchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SwitchProfileHolder(itemBinding, viewModel)
    }

    override fun onBindViewHolder(holder: SwitchProfileHolder, position: Int) {
        val user: User = placementList[position]
        holder.bind(user, position)
    }

    override fun getItemCount(): Int = placementList.size

    class SwitchProfileHolder(private val itemBinding: ItemSwitchUserBinding, val viewModel: ProfileViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(user: User, position: Int) {

            itemBinding.position = position
            itemBinding.viewModel = viewModel
            itemBinding.model = user
            if (user.lastAuth ) {
                itemBinding.currentUser.visible(false)
            } else {
                itemBinding.currentUser.gone(false)
            }
            if (user.photoPath.length > 0) {
                itemBinding.profileImage.setPadding(0)
                itemBinding.profileImage.setImageURI(Uri.parse(user.photoPath))
            } else {
                itemBinding.profileImage.setPadding(16.dp)
                itemBinding.profileImage.setImageDrawable(itemBinding.root.context.resources.getDrawable(
                    R.drawable.ic_profile))
            }
        }
    }
}