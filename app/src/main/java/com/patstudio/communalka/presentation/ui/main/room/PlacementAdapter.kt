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
import com.patstudio.communalka.databinding.ActivityAuthBinding.inflate
import com.patstudio.communalka.databinding.ItemPlacementBinding
import com.patstudio.communalka.databinding.ItemServicePaymentBinding
import com.patstudio.communalka.presentation.ui.main.profile.welcome.WelcomeViewModel
import com.skydoves.balloon.*
import gone
import visible
import android.view.View

import android.widget.TextView




class PlacementAdapter(private val placementList: List<Placement>,  val context: Context,  val viewModel: WelcomeViewModel) : RecyclerView.Adapter<PlacementAdapter.PlacementHolder>() {

    lateinit var res: Resources
    var value: Float = 0.0f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementHolder {
        res = parent.resources
        value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, res.getDisplayMetrics())
        val itemBinding =
            ItemPlacementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacementHolder(itemBinding, value, context, viewModel, parent)
    }

    override fun onBindViewHolder(holder: PlacementHolder, position: Int) {
        val placement: Placement = placementList[position]
        holder.bind(placement, position)
    }

    override fun getItemCount(): Int = placementList.size

    class PlacementHolder(private val itemBinding: ItemPlacementBinding, private val value: Float, private val context: Context, val viewModel: WelcomeViewModel, parent: ViewGroup) :
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

            itemBinding.servicePaymentsContainer.removeAllViews()
            var paymentSum = 0f
            placement.accounts.map {
                paymentSum += it.debtOfMoney
                Log.d("PlacementAdapter", "debt "+ it.debtOfMoney.toString().plus(" ₽"))
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val servicePaymentBinding: View = inflater.inflate(R.layout.item_service_payment, null)
                servicePaymentBinding.findViewById<TextView>(R.id.serviceName).text = it.serviceName
                servicePaymentBinding.findViewById<TextView>(R.id.message).text = it.message
                servicePaymentBinding.findViewById<TextView>(R.id.payment).text = it.debtOfMoney.toString().plus(" ₽")
                Log.d("PlacementAdapter", "debt "+ it.debtOfMoney.toString().plus(" ₽"))
                itemBinding.servicePaymentsContainer.addView(servicePaymentBinding)
            }


             paymentSum = 1f

            if (paymentSum > 0f) {
                itemBinding.paymentButton.background = itemBinding.root.context.resources.getDrawable(R.drawable.background_rounded_blue)
                itemBinding.paymentAmount.visible(false)
                itemBinding.paymentAmount.text = paymentSum.toString().plus(" ₽")
                itemBinding.paymentButton.setOnClickListener {
                    viewModel.selectPayment(placement)
                }
            } else {
                itemBinding.paymentButton.background = itemBinding.root.context.resources.getDrawable(R.drawable.gray_button_disable_background)
                itemBinding.paymentAmount.gone(false)
                itemBinding.paymentButton.setOnClickListener {
                }
            }

        }
    }


}