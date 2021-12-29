package com.patstudio.communalka.presentation.ui.main.readings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.R
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.PlacementMeter
import com.patstudio.communalka.data.model.Service
import com.patstudio.communalka.databinding.FragmentAccrualBinding
import com.patstudio.communalka.databinding.FragmentConsumptionHistoryBinding
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class AccrualFragment : Fragment() {

    private var _binding: FragmentAccrualBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AccrualViewModel>()
    private lateinit var adapter: MeterAccrualAdapter
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccrualBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initObservers() {
        viewModel.meters.observe(viewLifecycleOwner) {
            adapter = MeterAccrualAdapter(it, viewModel)
            binding.container.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
            binding.container.adapter = adapter
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
    }

    private fun initListeners() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel._toolbarWithTitle.postValue(Event(Pair(requireArguments().getString("placement")!!, requireArguments().getString("service")!!)))
        arguments?.getString("placement")?.let {
            Log.d("AccrualFragment ", "placement ${it}")
            viewModel.setCurrentPlacement(it)
        }
        arguments?.getString("account")?.let {
            Log.d("AccrualFragment ", "account ${it}")
            viewModel.setCurrentService(it)
        }
        arguments?.getString("service")?.let {
            Log.d("AccrualFragment ", "service ${it}")
            viewModel.setCurrentMeter(it)
        }

        initObservers()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}