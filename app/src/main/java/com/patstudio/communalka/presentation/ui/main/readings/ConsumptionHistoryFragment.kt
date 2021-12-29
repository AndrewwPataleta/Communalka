package com.patstudio.communalka.presentation.ui.main.readings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.PlacementMeter
import com.patstudio.communalka.databinding.FragmentConsumptionHistoryBinding
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ConsumptionHistoryFragment : Fragment() {

    private var _binding: FragmentConsumptionHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<ConsumptionHistoryViewModel>()
    private lateinit var adapter: ConsumptionHistoryAdapter
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConsumptionHistoryBinding.inflate(inflater, container, false)
        var tableRow = TableRow(requireContext())
        return binding.root
    }

    private fun initObservers() {
        viewModel.consumptionHistory.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    adapter = ConsumptionHistoryAdapter(it, viewModel)
                    binding.container.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.container.adapter = adapter

                }
            }
        }

        viewModel.updatePosition.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   adapter.notifyItemChanged(it)
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

        mainViewModel._toolbarWithTitle.postValue(Event(Pair(requireArguments().getParcelable<PlacementMeter>("meter")!!.title, "${requireArguments().getString("placement")} ${requireArguments().getString("supplier")}")))

        arguments?.getParcelable<PlacementMeter>("meter")?.let {
            viewModel.setCurrentPlacementMeter(it)
        }
        initObservers()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}