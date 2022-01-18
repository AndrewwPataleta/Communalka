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
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentHelpUserBinding
import com.patstudio.communalka.databinding.FragmentPersonalInfoBinding
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PersonalInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpUserBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun initObservers() {

    }

    private fun initListeners() {
        binding.text.setOnClickListener {
            findNavController().navigate(R.id.toFaq)
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