package com.patstudio.communalka.presentation.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentPersonalInfoBinding
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.databinding.FragmentUserNotificationSettingsBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

class UserNotificationFragment : Fragment() {

    private var _binding: FragmentUserNotificationSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<UserNotificationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserNotificationSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun initObservers() {
        viewModel.getUser().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.enabled.isChecked = it.notificationEnable
                }
            }
        }
        viewModel.showProgress.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        binding.enabled.visibility = View.GONE
                        binding.progress.visibility = View.VISIBLE
                    } else {
                        binding.enabled.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.enabled.setOnCheckedChangeListener { compoundButton, b ->
            viewModel.changePushEnable(b)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initCurrentUser()
        initListeners()
        initObservers()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}