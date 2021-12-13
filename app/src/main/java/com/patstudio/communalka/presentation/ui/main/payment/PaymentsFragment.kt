package com.patstudio.communalka.presentation.ui.main.payment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentAboutAppBinding
import com.patstudio.communalka.databinding.FragmentPaymentsBinding
import com.patstudio.communalka.databinding.FragmentPersonalInfoBinding
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.MainActivity
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import com.patstudio.communalka.presentation.ui.main.profile.HistoryVersionViewModel
import com.patstudio.communalka.presentation.ui.main.room.PlacementAdapter
import gone
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import visible
import convertLongToTime
import dp
import kotlinx.android.synthetic.main.fragment_payments.*


class PaymentsFragment : Fragment() {

    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<PaymentsViewModel>()
    private lateinit var paymentsAdapter: PaymentHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPaymentsBinding.inflate(inflater, container, false)
        return binding.root
    }



    private fun initObservers() {
        viewModel.payments.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    paymentsAdapter = PaymentHistoryAdapter(it, viewModel)
                    binding.premisesList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.premisesList.adapter = paymentsAdapter
                }
            }
        }
          viewModel.actionReceipt.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://ofd.ru/sites/default/files/inline-images/obrazec_cheka.png")
                    startActivity(intent)
                }
            }
        }

        viewModel.openFilter.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        findNavController().navigate(com.patstudio.communalka.R.id.toFilter)
                    }
                }
            }
        }
        viewModel.confirmFilterModel.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.chipGroup.removeAllViews()
                    if (it != null) {
                        it.date?.let {
                            val chip= Chip(requireContext())
                            chip.text = convertLongToTime(it.first).plus(" - ")+convertLongToTime(it.second)
                            chip.isCloseIconVisible = true
                            chip.chipStrokeWidth = 1f
                            chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                            chip.setOnCloseIconClickListener {
                                viewModel.removeDateFromFilter()
                            }
                            binding.chipGroup.addView(chip)
                        }
                        it.placement.second.map { placement ->
                            if (placement.selected) {
                                val chip= Chip(requireContext())
                                chip.text = placement.name
                                chip.chipStrokeWidth = 1f
                                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                                chip.isCloseIconVisible = true
                                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                                chip.setOnCloseIconClickListener {
                                    viewModel.removePlacementFromFilter(placement)
                                }
                                binding.chipGroup.addView(chip)
                            }
                        }
                        it.suppliers.second.map { supplier ->
                            if (supplier.selected) {
                                val chip= Chip(requireContext())
                                chip.text = supplier.name
                                chip.isCloseIconVisible = true
                                chip.chipStrokeWidth = 1f
                                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                                chip.setOnCloseIconClickListener {
                                    viewModel.removeSupplierFromFilter(supplier)
                                }
                                binding.chipGroup.addView(chip)
                            }
                        }
                        it.services.second.map { service ->
                            if (service.selected) {
                                val chip= Chip(requireContext())
                                chip.text = service.name
                                chip.isCloseIconVisible = true
                                chip.chipStrokeWidth = 1f
                                chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_dark))
                                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                                chip.setOnCloseIconClickListener {
                                    viewModel.removeServiceFromFilter(service)
                                }
                                binding.chipGroup.addView(chip)
                            }
                        }
                    }
                }
            }
        }

        viewModel.showProgress.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   if (it) {
                       binding.premisesList.gone(false)
                       binding.progress.visible(false)
                   } else {
                        binding.premisesList.visible(false)
                        binding.progress.gone(false)
                   }
                }
            }
        }
    }

    private fun initListeners() {

    }


    override fun onResume() {
        super.onResume()
        viewModel.updateFilters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}