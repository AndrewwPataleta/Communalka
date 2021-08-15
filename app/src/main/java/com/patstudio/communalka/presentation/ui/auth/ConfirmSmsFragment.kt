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
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    try {
                        Log.d("ConfirmSmsFragment","available sms "+it)
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
            }
        }
        viewModel.getCountDownTimer().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    Log.d("ConfirmSmsFramgment", "set timer "+it)
                    try {
                        binding.repeatSmsSendBtn.gone(false)
                        binding.repeatSendAfterValue.text = it
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        viewModel.getCongratulation().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                  if (it != null) {
                      binding.congratulation.visible(false)
                      val splitFio = it.split(" ")
                      binding.congratulation.text = getString(R.string.welcome_user, (splitFio[0]+" "+splitFio[1]))
                  } else {
                      binding.congratulation.gone(false)
                  }
                }
            }

        }

        viewModel.getClearSmsForm().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it != null) {
                        if (it) {
                            binding.smsEdit.text.clear();
                        }
                    }
                }
            }

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
                    Log.d("ConfirmSmsFragment", "open Pin Code")
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
        viewModel.getAvailableEmailSendSms().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.repeatSendAfterText.gone(false)
                    binding.repeatSendAfterValue.gone(false)
                    binding.repeatSmsSendBtn.gone(false)
                    binding.sendWithEmail.visible(false)
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
            requireActivity().onBackPressed()
        }
        binding.sendWithEmail.setOnClickListener{
            val bundle = bundleOf("type" to "email")
            findNavController().navigate(R.id.toLogin, bundle)
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