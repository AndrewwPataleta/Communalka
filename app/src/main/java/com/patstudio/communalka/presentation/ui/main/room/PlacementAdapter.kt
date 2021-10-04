package com.patstudio.communalka.presentation.ui.main.room

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.presentation.ui.main.profile.welcome.WelcomeViewModel
import com.skydoves.balloon.*
import gone
import visible

class PlacementAdapter(private val placementList: List<Placement>,  val context: Context,  val viewModel: WelcomeViewModel) : RecyclerView.Adapter<PlacementAdapter.PlacementHolder>() {

    lateinit var res: Resources
    var value: Float = 0.0f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        res = parent.resources
        value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, res.getDisplayMetrics())
        val itemBinding =
            ItemPlacementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, value, context, viewModel)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val placement: Placement = placementList[position]
        holder.bind(placement, position)
    }

    override fun getItemCount(): Int = placementList.size

    class PlacementHolder(private val itemBinding: ItemPlacementBinding, private val value: Float, private val context: Context, val viewModel: WelcomeViewModel) :
        RecyclerView.ViewHolder(itemBinding.root) {



        fun bind(placement: Placement, position: Int) {

            itemBinding.model = placement
            itemBinding.viewModel = viewModel
            itemBinding.position = position

            var meterSize = 0
            placement.accounts.map {
               meterSize += it.meters.size
            }

            if (meterSize == 0) {
                itemBinding.transmitTestimony.background = itemBinding.root.context.resources.getDrawable(R.drawable.background_transmission_readings_btn_disable)
                itemBinding.transmissioReadingText.setTextColor(itemBinding.root.context.resources.getColor(R.color.gray_placeholder))
                itemBinding.icon.setColorFilter(itemBinding.root.context.resources.getColor(R.color.gray_placeholder))
                itemBinding.transmitTestimony.setOnClickListener{}
            } else {
                itemBinding.transmitTestimony.background = itemBinding.root.context.resources.getDrawable(R.drawable.background_transmission_readings_btn)
                itemBinding.transmissioReadingText.setTextColor(itemBinding.root.context.resources.getColor(R.color.dark_blue))
                itemBinding.icon.setColorFilter(itemBinding.root.context.resources.getColor(R.color.dark_blue))
                itemBinding.transmitTestimony.setOnClickListener{viewModel.selectTransmissionReading(placement)}
            }

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

            if (!placement.isOpened) {
                itemBinding.arrow.rotation = 0f
                itemBinding.addressRoom.gone(false)
                itemBinding.edit.gone(false)
            } else {
                itemBinding.arrow.rotation = 180f
                itemBinding.addressRoom.visible(false)
                itemBinding.edit.visible(false)
            }
        }
    }
}