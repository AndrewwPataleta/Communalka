package com.patstudio.communalka.presentation.ui.auth

import android.app.AlertDialog
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
import com.patstudio.communalka.databinding.FragmentRestoreBinding
import gone
import invisible
import org.koin.android.viewmodel.ext.android.viewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import visible

class RestorePinFragment : Fragment() {

    private var _binding: FragmentRestoreBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<RestoreViewModel>()
    val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
    val watcher: FormatWatcher = MaskFormatWatcher(mask)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRestoreBinding.inflate(inflater, container, false)
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
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("phone" to it.phone, "type" to "Login", "restore" to it.restore)
                    findNavController().navigate(R.id.ConfirmSms, bundle)
                }

            }

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
        viewModel.getProgressPhoneSending().observe(requireActivity()) {

            if (it) {
                binding.restore.invisible(false)
                binding.progress.visible(false)
            } else {
                binding.restore.visible(false)
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

    }

    private fun disableNavigationListeners() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigationListeners()

        binding.phoneEdit.doAfterTextChanged {
            viewModel.setPhoneNumber(it.toString())
        }
        binding.close.setOnClickListener {
            binding.phoneEdit.setText("")
        }
        binding.restore.setOnClickListener {
          viewModel.restore()
        }

        //watcher.installOn(binding.phoneEdit)

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}