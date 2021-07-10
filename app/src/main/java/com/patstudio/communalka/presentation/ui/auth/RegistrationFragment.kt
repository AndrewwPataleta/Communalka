package com.patstudio.communalka.presentation.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentLoginBinding
import com.patstudio.communalka.databinding.FragmentRegistrationBinding
import gone
import invisible
import org.koin.android.viewmodel.ext.android.viewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import visible


class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun initNavigationListeners() {
        binding.registrationText.setOnClickListener {
            findNavController().navigate(R.id.toLogin)
        }
        binding.registration.setOnClickListener {
           viewModel.registration()
        }
    }

    private fun initObservers() {
        viewModel.getPhoneError().observe(requireActivity()) {
            if (it) {
                Toast.makeText(requireContext(), getString(R.string.check_correct_input_data), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.getProgressPhoneSending().observe(requireActivity()) {

            if (it) {
                binding.registration.invisible(false)
                binding.progress.visible(false)
            } else {
                binding.registration.visible(false)
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
        viewModel.getSmsCode().observe(requireActivity()) {
            findNavController().navigate(R.id.toConfirmSms)
        }
    }

    private fun disableNavigationListeners() {
        binding.registrationText.setOnClickListener(null)
        binding.registration.setOnClickListener(null)
    }

    private fun setPhoneMask() {
        val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
        val watcher: FormatWatcher = MaskFormatWatcher(mask)
        watcher.installOn(binding.phoneEdit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigationListeners()
        setPhoneMask()

        binding.phoneEdit.doAfterTextChanged {
            viewModel.setPhoneNumber(it.toString())
        }

        binding.emailEdit.doAfterTextChanged {
            viewModel.setUserEmail(it.toString())
        }

        binding.fioEdit.doAfterTextChanged {
            viewModel.setUserFio(it.toString())
        }

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}