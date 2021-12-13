package com.patstudio.communalka.presentation.ui.main.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.*
import com.patstudio.communalka.presentation.ui.main.room.PlacementMeterAdapter
import convertLongToTime
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import setAllOnClickListener
import java.text.SimpleDateFormat
import java.util.*

class FilterOrderFragment : Fragment() {

    private var _binding: FragmentFilterPaymentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<PaymentsViewModel>()
    private lateinit var paymentsAdapter: PaymentHistoryAdapter
    private  var filterSupplierAdapter: FilterSupplierAdapter? = null
    private  var filterPlacementAdapter: FilterPlacementAdapter? = null
    private  var filterServiceAdapter: FilterServiceAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFilterPaymentsBinding.inflate(inflater, container, false)
        viewModel.updateFilters()
        return binding.root
    }


    private fun initObservers() {
        viewModel.filterModel.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it.date != null) {
                        this.binding.dateRangeValue.text = convertLongToTime(it.date!!.first).plus(" - ")+convertLongToTime(it.date!!.second)
                    } else {
                        this.binding.dateRangeValue.text = ""
                    }
                    if (it.suppliers.first) {
                        filterSupplierAdapter = FilterSupplierAdapter(it.suppliers.second, viewModel)
                        binding.supplierContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                        binding.supplierContainer.adapter = filterSupplierAdapter
                        binding.supplierDownIcon.rotation = 0f
                        binding.supplierContainer.visibility = View.VISIBLE
                    } else {
                        binding.supplierContainer.visibility = View.GONE
                        binding.supplierDownIcon.rotation = 180f
                    }
                    if (it.placement.first) {
                        filterPlacementAdapter = FilterPlacementAdapter(it.placement.second, viewModel)
                        binding.placementContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                        binding.placementContainer.adapter = filterPlacementAdapter
                        binding.placementDownIcon.rotation = 0f
                        binding.placementContainer.visibility = View.VISIBLE
                    } else {
                        binding.placementContainer.visibility = View.GONE
                        binding.placementDownIcon.rotation = 180f
                    }
                    if (it.services.first) {
                        filterServiceAdapter = FilterServiceAdapter(it.services.second, viewModel)
                        binding.serviceContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                        binding.serviceContainer.adapter = filterServiceAdapter
                        binding.serviceDownIcon.rotation = 0f
                        binding.serviceContainer.visibility = View.VISIBLE
                    } else {
                        binding.serviceContainer.visibility = View.GONE
                        binding.serviceDownIcon.rotation = 180f
                    }
                }
            }
        }
        viewModel.filterModel.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    filterPlacementAdapter?.let {
                        it.notifyDataSetChanged()
                    }
                    filterSupplierAdapter?.let {
                        it.notifyDataSetChanged()
                    }
                    filterServiceAdapter?.let {
                        it.notifyDataSetChanged()
                    }
                }
            }
        }

        viewModel.backScreen.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   requireActivity().onBackPressed()
                }
            }
        }
    }




    private fun initListeners() {
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.reset.setOnClickListener {
            viewModel.resetFilter()
        }
        binding.supplierDownIcon.setOnClickListener {
            viewModel.updateSupplierVisible()
        }
        binding.accept.setOnClickListener {
            viewModel.acceptFilter()
        }
        binding.serviceDownIcon.setOnClickListener {
            viewModel.updateServiceVisible()
        }
        binding.placementDownIcon.setOnClickListener {
            viewModel.updatePlacementVisible()
        }
        binding.dateGroup.setAllOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Выберите диапазон дат")
                    .build()

            dateRangePicker.addOnPositiveButtonClickListener {
                viewModel.updateFilterDate(it)
            }

            dateRangePicker.show(requireActivity().supportFragmentManager, "tag")
        }
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