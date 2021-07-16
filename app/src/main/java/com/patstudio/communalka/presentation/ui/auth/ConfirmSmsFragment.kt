package com.patstudio.communalka.presentation.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.databinding.FragmentConfirmSmsBinding
import com.patstudio.communalka.databinding.FragmentLoginBinding
import com.patstudio.communalka.databinding.FragmentRegistrationBinding
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible
import java.lang.Exception


class ConfirmSmsFragment : Fragment() {

    private var _binding: FragmentConfirmSmsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<ConfirmViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirmSmsBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun initObservers() {
        viewModel.getAvailableSendSms().observe(requireActivity()) {
            if (it) {
                binding.repeatSendAfterText.gone(false)
                binding.repeatSendAfterValue.gone(false)
                binding.repeatSmsSendBtn.visible(false)
            } else {
                binding.repeatSendAfterText.visible(false)
                binding.repeatSendAfterValue.visible(false)
                binding.repeatSmsSendBtn.gone(false)
            }
        }
        viewModel.getCountDownTimer().observe(requireActivity()) {
            try {

                binding.repeatSendAfterValue.text = it
            } catch (e: Exception) {}
        }
        viewModel.getProgressSmsCodeSending().observe(requireActivity()) {
            if (it) {
                binding.progressSmsSending.visible(true)
                binding.smsEdit.isEnabled = false
            } else {
                binding.progressSmsSending.gone(true)
                binding.smsEdit.isEnabled = true
            }
        }
        viewModel.getUserForm().observe(requireActivity()) {
            val bundle = bundleOf("user" to it)
            findNavController().navigate(R.id.PinCode, bundle)
        }

        viewModel.getUserMessage().observe(requireActivity()) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(it)
            builder.setPositiveButton("Ок"){dialogInterface, which ->
                dialogInterface.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    private fun initListeners() {
        binding.repeatSmsSendBtn.setOnClickListener {
            viewModel.repeatSendSms()
        }
        binding.smsEdit.doAfterTextChanged {
            viewModel.setSmsCode(it.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destoyTimer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("phone")?.let {
            viewModel.setPhone(it)
        }
        arguments?.getParcelable<UserForm>("user")?.let {
            viewModel.setUserForm(it)
        }
        arguments?.getString("type")?.let {
            viewModel.setFormType(it)
        }
        initListeners()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}