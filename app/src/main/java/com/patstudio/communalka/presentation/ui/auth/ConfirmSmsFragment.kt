package com.patstudio.communalka.presentation.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        viewModel.getAvailableSendSms().observe(this) {
            try {
                if (it) {
                    binding.repeatSendAfterText.gone(false)
                    binding.repeatSendAfterValue.gone(false)
                    binding.repeatSmsSendBtn.visible(false)
                } else {
                    binding.repeatSendAfterText.visible(false)
                    binding.repeatSendAfterValue.visible(false)
                    binding.repeatSmsSendBtn.gone(false)
                }
            } catch (e: Exception) {}
        }
        viewModel.getCountDownTimer().observe(this) {
            try {

                binding.repeatSendAfterValue.text = it
            } catch (e: Exception) {}
        }

        viewModel.getCongratulation().observe(this) {
            binding.congratulation.text = getString(R.string.welcome_user, it)
        }
        viewModel.getProgressSmsCodeSending().observe(this) {
            try {
                if (it) {
                    binding.progressSmsSending.visible(true)
                    binding.smsEdit.isEnabled = false
                } else {
                    binding.progressSmsSending.gone(true)
                    binding.smsEdit.isEnabled = true
                }
            } catch (e: Exception) { }
        }
        viewModel.getUserForm().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("user" to it)
                    findNavController().navigate(R.id.PinCode, bundle)
                }
            }
        }
        viewModel.getSmsCode().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   binding.smsEdit.setText(it)
                }
            }
        }
        viewModel.getUserMessage().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val builder = MaterialAlertDialogBuilder(requireContext())
                    builder.setTitle(it)
                    builder.setPositiveButton("Отправить повторно"){dialogInterface, which ->
                        viewModel.repeatSendSms()
                        dialogInterface.dismiss()
                    }
                    builder.setNegativeButton("Отмена"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    builder.setCancelable(false)
                    builder.show()
                }
            }
        }
    }

    private fun initListeners() {
        binding.repeatSmsSendBtn.setOnClickListener {
            viewModel.repeatSendSms()
        }
        binding.smsEdit.doAfterTextChanged {
            viewModel.setSmsCode(it.toString())
        }
        binding.close.setOnClickListener {
            binding.smsEdit.setText("")
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
        arguments?.getBoolean("restore")?.let {
            viewModel.setIsRestore(it)
        }
        initListeners()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}