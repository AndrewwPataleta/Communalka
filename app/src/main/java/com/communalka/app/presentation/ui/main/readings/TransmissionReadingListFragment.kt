package com.communalka.app.presentation.ui.main.readings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.communalka.app.R
import com.communalka.app.data.model.Placement
import com.communalka.app.databinding.FragmentTransmissionReadingListBinding
import com.communalka.app.presentation.ui.main.room.PlacementMeterAdapter
import com.communalka.app.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
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
                    Log.d("Tramission", "adapter size ${it.size}")
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