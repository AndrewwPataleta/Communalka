package com.patstudio.communalka.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentLoginBinding
import gone
import invisible
import org.koin.android.viewmodel.ext.android.viewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import visible

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun initObservers() {
        viewModel.getPhoneError().observe(requireActivity()) {
            if (it) {
                Toast.makeText(requireContext(), getString(R.string.check_phone_number), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.getConfirmCode().observe(requireActivity()) {
            findNavController().navigate(R.id.PinCode)
        }
        viewModel.getConfirmSmsParams().observe(requireActivity()) {
            val bundle = bundleOf("phone" to it.phone)
            findNavController().navigate(R.id.ConfirmSms, bundle)

        }
        viewModel.getProgressPhoneSending().observe(requireActivity()) {

            if (it) {
                binding.login.invisible(false)
                binding.progress.visible(false)
            } else {
                binding.login.visible(false)
                binding.progress.gone(false)
            }
        }
        viewModel.getDisableNavigation().observe(requireActivity()) {
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

        binding.phoneEdit.doAfterTextChanged {
            viewModel.setPhoneNumber(it.toString())
        }

        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher: FormatWatcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneEdit)

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}