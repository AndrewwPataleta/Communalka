package com.patstudio.communalka.presentation.ui.main.readings


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.setPadding
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.presentation.ui.main.payment.PaymentPlacementViewModel
import dp


class PlacementSelectorPaymentAdapter(var context: Context, var placements: ArrayList<Placement>, var viewModel: PaymentPlacementViewModel) :
    BaseAdapter() {

    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return placements.size
    }

    override fun getItem(position: Int): Any {
        return placements[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        var placement: Placement = placements.get(position)
        view = this.mInflator.inflate(R.layout.item_placement_selector, parent, false)
        view.findViewById<TextView>(R.id.placementName).text = placement.name
        view.findViewById<TextView>(R.id.addressRoom).text = placement.address


        when (placement.imageType) {
            "DEFAULT" -> {
                when (placement.path) {
                    "HOME" -> {
                        view.findViewById<ImageView>(R.id.placementImage).setImageDrawable(
                            context.getDrawable(
                                R.drawable.ic_home
                            )
                        )
                    }
                    "ROOM" -> {
                        view.findViewById<ImageView>(R.id.placementImage).setImageDrawable(
                            context.getDrawable(
                                R.drawable.ic_room
                            )
                        )
                    }
                    "OFFICE" -> {
                        view.findViewById<ImageView>(R.id.placementImage).setImageDrawable(
                            context.getDrawable(
                                R.drawable.ic_office
                            )
                        )
                    }
                    "HOUSE" -> {
                        view.findViewById<ImageView>(R.id.placementImage).setImageDrawable(
                            context.getDrawable(
                                R.drawable.ic_country_house
                            )
                        )
                    }
                }
            }
            "STORAGE" -> {
                view.findViewById<ImageView>(R.id.placementImage).setPadding(0)
                view.findViewById<ImageView>(R.id.placementImage).setImageURI(placement.path.toUri())
            }
        }

//        view.findViewById<View>(R.id.placementRoot).setOnClickListener {
//            viewModel.selectedPlacement(placement)
//        }
        return view
    }

}