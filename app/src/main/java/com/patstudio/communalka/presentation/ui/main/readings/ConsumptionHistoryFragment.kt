package com.patstudio.communalka.presentation.ui.main.readings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.R
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.PlacementMeter
import com.patstudio.communalka.databinding.FragmentConsumptionHistoryBinding
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ConsumptionHistoryFragment : Fragment() {

    private var _binding: FragmentConsumptionHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<ConsumptionHistoryViewModel>()
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

        viewModel.pdfDownload.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("model" to it)
                    NavHostFragment.findNavController(this).navigate(R.id.toWeb, bundle)
//                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
//                    startActivity(browserIntent)
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