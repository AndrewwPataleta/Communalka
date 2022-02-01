package com.patstudio.communalka.presentation.ui.main.readings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.FragmentTransmissionReadingListBinding
import com.patstudio.communalka.presentation.ui.main.room.PlacementAdapter
import com.patstudio.communalka.presentation.ui.main.room.PlacementMeterAdapter
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener





class TransmissionReadingListFragment : Fragment() {

    private var _binding: FragmentTransmissionReadingListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<TransmissionReadingListViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()
    private lateinit var adater: PlacementMeterAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTransmissionReadingListBinding.inflate(inflater, container, false)


        return binding.root
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initObservers() {
        viewModel.currentPlacement.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.model = it
                }
            }
        }
        viewModel.placementMeters.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {

                    adater = PlacementMeterAdapter(it, requireContext(),viewModel)
                    binding.meterContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.meterContainer.adapter = adater
                }
            }
        }
        viewModel.placementsList.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val adapter = PlacementSelectorAdapter(requireContext(), it, viewModel)
                    binding.placementSelector.onItemSelectedListener =
                        object : OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                viewModel.selectedPlacement(parent.getItemAtPosition(position) as Placement)
                            }
                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    binding.placementSelector.adapter = adapter
                }
            }
        }


        viewModel.transmissionPlacementMeter.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("meter" to it)
                    findNavController().navigate(R.id.toTransmissionReadingCounter, bundle)
                }
            }
        }
        viewModel.historyPlacementMeter.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("meter" to it.first, "placement" to it.third, "supplier" to it.second)
                    findNavController().navigate(R.id.toTransmissionHistory, bundle)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
       // viewModel.updateMeters()
    }

    private fun initListeners() {
//
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Placement>("placement")?.let {
            viewModel.setCurrentPlacement(it)
        }
        initObservers()
        initListeners()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}