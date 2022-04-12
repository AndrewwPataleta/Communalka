package com.communalka.app.presentation.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.communalka.app.R
import com.communalka.app.common.utils.gone
import com.communalka.app.common.utils.invisible
import com.communalka.app.common.utils.visible
import com.communalka.app.databinding.FragmentLoginBinding
import org.koin.android.viewmodel.ext.android.viewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<LoginViewModel>()
    val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
    val watcher: FormatWatcher = MaskFormatWatcher(mask)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun initObservers() {
        viewModel.getPhoneError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.phoneEdit.setError("Проверьте корректность введенных данных")
                }
            }
        }
        viewModel.getEmailError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.emailEdit.setError("Вы неправильно указали почту!")
                }
            }
        }
        viewModel.getConfirmCode().observe(this) {
            findNavController().navigate(R.id.PinCode)
        }
        viewModel.getConfirmSmsParams().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("phone" to it.phone, "type" to "Login")
                    findNavController().navigate(R.id.ConfirmSms, bundle)
                }

            }
        }

        viewModel.getLoginType().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   when (it) {
                       "default"-> {
                           binding.emailEdit.gone(false)
                           binding.phoneEdit.visible(false)
                           binding.phoneEdit.inputType = InputType.TYPE_CLASS_TEXT
                           binding.phoneEdit.hint = resources.getString(R.string.phone_hint)
                           binding.titleText.text = resources.getString(R.string.email_or_phone)
                           binding.phoneEdit.doAfterTextChanged {
                               viewModel.setPhoneNumber(binding.phoneEdit.text.toString())
                           }

                           binding.close.setOnClickListener {
                               requireActivity().onBackPressed()
                           }

                       }
                       "phone" -> {
                           binding.emailEdit.gone(false)
                           binding.phoneEdit.visible(false)
                           binding.phoneEdit.hint = resources.getString(R.string.phone_hint)
                           binding.phoneEdit.inputType = InputType.TYPE_CLASS_PHONE
                           binding.titleText.text = resources.getString(R.string.phone)
                           binding.phoneEdit.doAfterTextChanged {
                               viewModel.setPhoneNumber(watcher.mask.toUnformattedString())
                           }
                           binding.close.setOnClickListener {
                               requireActivity().onBackPressed()
                           }
                           watcher.installOn(binding.phoneEdit)
                       }
                       "email" -> {
                           binding.emailEdit.visible(false)
                           binding.phoneEdit.invisible(false)
                           binding.emailEdit.hint = resources.getString(R.string.email_hint)
                           binding.titleText.text = resources.getString(R.string.email_require)
                           binding.emailEdit.doAfterTextChanged {
                               viewModel.setEmail(it.toString())
                           }
                           binding.close.setOnClickListener {
                               requireActivity().onBackPressed()
                           }
                       }
                   }
                }

            }
        }
        viewModel.getUserMessage().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
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
        }
        viewModel.getProgressPhoneSending().observe(this) {

            if (it) {
                binding.login.invisible(false)
                binding.progress.visible(false)
            } else {
                binding.login.visible(false)
                binding.progress.gone(false)
            }
        }
        viewModel.getDisableNavigation().observe(this) {
            if (it) {
               disableNavigationListeners()
            } else {
               initNavigationListeners()
            }
        }
    }

    private fun initNavigationListeners() {
        binding.registrationText.setOnClickListener {
            findNavController().navigate(R.id.toRegistrationFragment)
        }
        binding.login.setOnClickListener {
            viewModel.login()
        }
    }



    private fun disableNavigationListeners() {
        binding.registrationText.setOnClickListener(null)
        binding.login.setOnClickListener(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigationListeners()
        initObservers()
        arguments?.getString("type", "default").let {
            viewModel.setLoginType(it)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}