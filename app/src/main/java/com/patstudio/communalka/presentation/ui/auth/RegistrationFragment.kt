package com.patstudio.communalka.presentation.ui.auth

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.R
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
    val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
    val watcher: FormatWatcher = MaskFormatWatcher(mask)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun initNavigationListeners() {
        binding.registrationText.setOnClickListener {
            val bundle = bundleOf("type" to "default")
            findNavController().navigate(R.id.toLogin, bundle)
        }
        binding.registration.setOnClickListener {
           viewModel.registration()
        }
        binding.userLicenceTextFirst.setOnClickListener {
           binding.userLicenceCheck.isChecked = !binding.userLicenceCheck.isChecked
        }
        binding.userLicenceTextSecond.setOnClickListener {
            binding.userLicenceCheck.isChecked = !binding.userLicenceCheck.isChecked
        }
        binding.userLicenceLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/gview?embedded=true&url="+BuildConfig.API_HOST+"/public_offer/"))
            startActivity(browserIntent)
        }
        binding.close.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initObservers() {
        viewModel.getPhoneError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.phoneEdit.setError(it)
                }
            }
        }

        viewModel.getUserEmailError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.emailEdit.setError(it)
                }
            }
        }

        viewModel.getUserFioError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.fioEdit.setError(it)
                }
            }

        }
        viewModel.getProgressPhoneSending().observe(this) {

            if (it) {
                binding.registration.invisible(false)
                binding.progress.visible(false)
            } else {
                binding.registration.visible(false)
                binding.progress.gone(false)
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
        viewModel.getDisableNavigation().observe(this) {
            if (it) {
                disableNavigationListeners()
            } else {
                initNavigationListeners()
            }
        }
        viewModel.getLicenseError().observe(this) {
          this.binding.userLicenceTextFirst.setTextColor(resources.getColor(android.R.color.holo_red_dark))
          this.binding.userLicenceTextSecond.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }
        viewModel.getUserForm().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("user" to it, "type" to "Registration")
                    findNavController().navigate(R.id.ConfirmSms, bundle)
                }
            }

        }
    }

    private fun disableNavigationListeners() {
        binding.registrationText.setOnClickListener(null)
        binding.registration.setOnClickListener(null)
    }

    private fun setPhoneMask() {


        watcher.installOn(binding.phoneEdit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavigationListeners()
        setPhoneMask()

        binding.phoneEdit.doAfterTextChanged {
            viewModel.setPhoneNumber(watcher.mask.toUnformattedString())
        }

        binding.emailEdit.doAfterTextChanged {
            viewModel.setUserEmail(it.toString())
        }

        binding.fioEdit.doAfterTextChanged {
            viewModel.setUserFio(it.toString())
        }

        binding.userLicenceCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.userLicenseAgreement(isChecked)
        }
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}