package com.communalka.app.presentation.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.communalka.app.R
import com.communalka.app.common.utils.gone
import com.communalka.app.common.utils.visible
import com.communalka.app.data.model.UserForm
import com.communalka.app.databinding.FragmentConfirmSmsBinding

import org.koin.android.viewmodel.ext.android.viewModel

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
        viewModel.getAvailableSendSms().observe(viewLifecycleOwner,  {

            if (!it.hasBeenHandled.get()) {
                try {
                    it.getContentIfNotHandled {
                        if (it) {
                            binding.repeatSendAfterText.visibility = View.GONE
                            binding.repeatSendAfterValue.visibility = View.GONE
                            binding.repeatSmsSendBtn.visibility = View.VISIBLE
                        } else {
                            binding.repeatSendAfterText.visibility = View.VISIBLE
                            binding.repeatSendAfterValue.visibility = View.VISIBLE
                            binding.repeatSmsSendBtn.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        });

        viewModel.getCountDownTimer().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    try {
                        binding.repeatSendAfterValue.text = it
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        viewModel.getCongratulation().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                  if (it != null) {
                      binding.congratulation.visible(false)
                      val splitFio = it.trim().split(" ")

                      var fio = ""
                      if (splitFio.size > 2) {
                          splitFio[1].let {
                              fio+=it
                          }
                          splitFio[2].let {
                              fio += " "+it
                          }
                      } else {
                          splitFio[1].let {
                              fio+=it
                          }
                      }

                      binding.congratulation.text = getString(R.string.welcome_user, fio)
                  } else {
                      binding.congratulation.gone(false)
                  }
                }
            }

        }

        viewModel.getClearSmsForm().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it != null) {
                        if (it) {
                            binding.smsEdit.text.clear()
                        }
                    }
                }
            }

        }
        viewModel.getProgressSmsCodeSending().observe(viewLifecycleOwner) {
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
                    if (it.second) {
                        val bundle = bundleOf("user" to it.first)
                        findNavController().navigate(R.id.PinCode, bundle)
                    } else {
                        val bundle = bundleOf("user" to it.first)
                        findNavController().navigate(R.id.toWelcomePage, bundle)
                    }

                }
            }
        }
        viewModel.getSmsCode().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   binding.smsEdit.setText(it)
                }
            }
        }
        viewModel.getAvailableEmailSendSms().observe(viewLifecycleOwner) {

            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        binding.repeatSendAfterText.gone(false)
                        binding.repeatSendAfterValue.gone(false)
                        binding.repeatSmsSendBtn.gone(false)
                        binding.sendWithEmail.visible(false)
                    } else {
                        binding.repeatSendAfterText.visible(false)
                        binding.repeatSendAfterValue.visible(false)
                        binding.repeatSmsSendBtn.gone(false)
                        binding.sendWithEmail.gone(false)
                    }

                }
            }
        }
        viewModel.getUserMessage().observe(viewLifecycleOwner) {
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

        viewModel.getUserMessageWithoutButton().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val builder = MaterialAlertDialogBuilder(requireContext())
                    builder.setMessage(it)
                    builder.setPositiveButton("Ок"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    builder.setCancelable(false)
                    builder.show()
                }
            }
        }

        viewModel.getLoginByEmail().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("type" to "email")
                    findNavController().navigate(R.id.toLogin, bundle)
                }
            }
        }

        viewModel.getOpenDialogEmail().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val inputEditTextField = EditText(requireActivity())
                    inputEditTextField.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                    val dialog = AlertDialog.Builder(requireContext())
                        .setMessage("Укажите вашу почту")
                        .setView(inputEditTextField)
                        .setPositiveButton("Ок") { _, _ ->
                            val editTextInput = inputEditTextField.text.toString()
                            viewModel.setEmailFromDialog(editTextInput)
                        }

                        .create()
                    dialog.show()
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
        binding.sendWithEmail.setOnClickListener {
            viewModel.selectSentByEmail()
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