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
import android.widget.ImageView

import android.widget.TextView
import getServiceIcon
import roundOffTo2DecPlaces


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

            var fio = placement.fio.split(" ");

            var charArray = fio.get(1).toCharArray().get(0).plus(".")


            if (fio.size > 2) {
                charArray += fio.get(2).toCharArray().get(0)
            }
            
            itemBinding.ownerName.setText(fio.get(0)+" "+charArray)

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
                    itemBinding.placementImage.setImageURI(placement.path.toUri())
                }
            }

            itemBinding.servicePaymentsContainer.removeAllViews()


            if (!placement.isOpened) {
                itemBinding.arrow.rotation = 0f
                itemBinding.addressRoom.gone(false)
                itemBinding.edit.gone(false)

            } else {
                itemBinding.arrow.rotation = 180f
                itemBinding.addressRoom.visible(false)
                itemBinding.edit.visible(false)
                placement.invoices?.map {

                    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val servicePaymentBinding: View = inflater.inflate(R.layout.item_service_payment, null)
                    val service = it.service

                    servicePaymentBinding.findViewById<TextView>(R.id.serviceName).text = it.service

                    placement.accounts.map { account->

                        if (account.supplierName.compareTo(it.supplier) == 0) {

                            servicePaymentBinding.setOnClickListener {
                                if (account.active) {
                                    viewModel.selectDetailService(placement = placement.name, service = service, account = account.id)
                                }
                            }

                            if (account.message.isNotEmpty()) {
                                servicePaymentBinding.findViewById<TextView>(R.id.message).text = account.message
                            } else {
                                account.meters.map {
                                    if (it.account.compareTo(account.id) == 0) {
                                        servicePaymentBinding.findViewById<TextView>(R.id.message).text = "Тек. показания: ${it.last_values.lastValue}"
                                }
                            }
                        }
                    } }

                    servicePaymentBinding.findViewById<TextView>(R.id.payment).text = (it.balance+it.penalty).toString().plus(" ₽")
                    servicePaymentBinding.findViewById<ImageView>(R.id.image).setImageDrawable(getServiceIcon(service, itemBinding.root.context))
                    itemBinding.servicePaymentsContainer.addView(servicePaymentBinding)
                }
            }

            var penaltySum = 0.0

            placement.invoices?.map {
                penaltySum += it.penalty+it.balance
            }

            if (penaltySum > 0) {
                itemBinding.paymentButton.background = itemBinding.root.context.resources.getDrawable(R.drawable.background_rounded_blue)
                itemBinding.paymentAmount.visible(false)
                itemBinding.paymentStatus.gone(false)
                itemBinding.paymentAmount.text = "${roundOffTo2DecPlaces(penaltySum.toFloat())} ₽"
                itemBinding.paymentButton.setOnClickListener {
                    viewModel.selectPayment(placement)
                }
            } else {
                itemBinding.paymentButton.background = itemBinding.root.context.resources.getDrawable(R.drawable.gray_button_disable_background)
                itemBinding.paymentAmount.gone(false)
                itemBinding.paymentButton.setOnClickListener {}
            }

        }
    }


}