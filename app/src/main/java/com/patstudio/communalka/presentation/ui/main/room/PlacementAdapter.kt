package com.patstudio.communalka.presentation.ui.main.room

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.skydoves.balloon.*
import it.sephiroth.android.library.xtooltip.Tooltip

class PlacementAdapter(private val placementList: List<Placement>,  val context: Context, val lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<PlacementAdapter.PlacementHolder>() {

    lateinit var res: Resources
    var value: Float = 0.0f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        res = parent.resources
        value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, res.getDisplayMetrics())
        val itemBinding =
            ItemPlacementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, value, context, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val placement: Placement = placementList[position]
        holder.bind(placement)
    }

    override fun getItemCount(): Int = placementList.size

    class PlacementHolder(private val itemBinding: ItemPlacementBinding, private val value: Float, private val context: Context, private val lifecycleOwner: LifecycleOwner) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(placement: Placement) {

            itemBinding.placementName.text = placement.name
            when (placement.imageType) {
                "DEFAULT" -> {
                    itemBinding.placementImage.setPadding(value.toInt())
                    when (placement.path) {

                        "HOME" -> {
                            itemBinding.placementImage.setImageDrawable(itemBinding.root.context.resources.getDrawable(R.drawable.ic_home))
                        }
                        "ROOM" -> {
                            itemBinding.placementImage.setImageDrawable(itemBinding.root.context.resources.getDrawable(R.drawable.ic_room))
                        }
                        "OFFICE" -> {
                            itemBinding.placementImage.setImageDrawable(itemBinding.root.context.resources.getDrawable(R.drawable.ic_office))
                        }
                        "HOUSE" -> {
                            itemBinding.placementImage.setImageDrawable(itemBinding.root.context.resources.getDrawable(R.drawable.ic_country_house))
                        }
                    }
                }
                "STORAGE" -> {
                    itemBinding.placementImage.setPadding(0)
                    Log.d("PlacementAdapter", "URI "+placement.path.toUri())
                    itemBinding.placementImage.setImageURI(placement.path.toUri())
                }
            }
        }
    }
}