package com.communalka.app.presentation.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.communalka.app.databinding.FragmentFaqBinding
import org.koin.android.viewmodel.ext.android.viewModel

class FaqFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FaqViewModel>()
    private lateinit var faqAdapter: FaqAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        viewModel.initFaq()
        return binding.root
    }


    private fun initObservers() {
        viewModel.progress.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        binding.contentContainer.visibility = View.GONE
                        binding.progress.visibility = View.VISIBLE
                    } else {
                        binding.contentContainer.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                    }
                }
            }
        }
        viewModel.faq.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    faqAdapter = FaqAdapter(it, viewModel)
                    binding.faqList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.faqList.adapter = faqAdapter
                }
            }
        }
    }

    private fun initListeners() {
        binding.search.addTextChangedListener {
            viewModel.searchByStr(it.toString())
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