package com.patstudio.communalka.presentation.ui.main.profile.security

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.common.utils.MaskFormatter
import com.patstudio.communalka.databinding.FragmentAboutAppBinding
import com.patstudio.communalka.databinding.FragmentEntranceSecurityBinding
import com.patstudio.communalka.databinding.FragmentPersonalInfoBinding
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import com.patstudio.communalka.presentation.ui.main.profile.HistoryVersionViewModel
import com.skydoves.balloon.extensions.dp
import gone
import maskEmail
import org.koin.android.viewmodel.ext.android.viewModel
import setAllOnClickListener
import visible

class EntranceSecurityFragment : Fragment() {

    private var _binding: FragmentEntranceSecurityBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<EntranceSecurityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEntranceSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun initObservers() {
        viewModel.initCurrentUser()
        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.model = it
                    binding.emailValue.text = maskEmail(it.email)
                    binding.phoneValue.text = it.phone
                }
            }
        }
    }

    private fun initListeners() {
        binding.switchAutoSignIn.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setAutoSignIn(isChecked)
        }
        binding.switchSigninFingerprint.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setFingerPrintAvailable(isChecked)
        }
//        binding.emailGroup.setAllOnClickListener(){
//            findNavController().navigate(R.id.toEditEmail)
//        }
        binding.pinCodeText.setOnClickListener(){
            findNavController().navigate(R.id.toEditPinCode)
        }
        binding.emailText.setOnClickListener(){
            findNavController().navigate(R.id.toEditEmail)
        }
        binding.phoneText.setOnClickListener(){
            findNavController().navigate(R.id.toEditPhone)
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