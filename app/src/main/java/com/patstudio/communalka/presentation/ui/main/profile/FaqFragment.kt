package com.patstudio.communalka.presentation.ui.main.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentFaqBinding
import com.patstudio.communalka.databinding.FragmentHelpUserBinding
import com.patstudio.communalka.databinding.FragmentPersonalInfoBinding
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

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