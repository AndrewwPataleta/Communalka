package com.communalka.app.presentation.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.communalka.app.databinding.FragmentHistoryVersionBinding
import org.koin.android.viewmodel.ext.android.viewModel

class HistoryVersionFragment : Fragment() {

    private var _binding: FragmentHistoryVersionBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<HistoryVersionViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryVersionBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun initObservers() {
        viewModel.getListVersionAppInfo().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val adapter = HistoryVersionAdapter(it, viewModel)
                    binding.historyVersionContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.historyVersionContainer.adapter = adapter

                }
            }
        }
    }

    private fun initListeners() {

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.initVersionAppInfo()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}