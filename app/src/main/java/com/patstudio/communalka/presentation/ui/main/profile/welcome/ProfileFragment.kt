package com.patstudio.communalka.presentation.ui.main.profile.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun setAuthNavigation() {
        this.binding.profileText.setOnClickListener {
            findNavController().navigate(R.id.PersonalInfo)
        }
    }

    private fun removeAuthNavigation() {
        this.binding.profileText.setOnClickListener {}
    }

    private fun haveNoAuthUser() {
        removeAuthNavigation()
        this.binding.avatar.gone(false)
        this.binding.userFio.gone(false)
        this.binding.iconDown.gone(false)
        this.binding.logoutGroup.gone(false)
        this.binding.loginBtn.visible(false)
        this.binding.profileText.setTextColor(resources.getColor(R.color.gray_dark))
        this.binding.cardsText.setTextColor(resources.getColor(R.color.gray_dark))
        this.binding.securityText.setTextColor(resources.getColor(R.color.gray_dark))
        this.binding.notificationText.setTextColor(resources.getColor(R.color.gray_dark))

        this.binding.profileArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)
        this.binding.cardsArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)
        this.binding.securityArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)
        this.binding.notificationArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)


    }

    private fun initObservers() {
        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    setAuthNavigation()
                    this.binding.userFio.text = it.name

                }
            }
        }
        viewModel.getHaveNoAuth().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    haveNoAuthUser()
                }
            }
        }
    }

    private fun initListeners() {
        this.binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.WelcomeFragment)
        }
        this.binding.profileText.setOnClickListener {
            findNavController().navigate(R.id.PersonalInfo)
        }
        this.binding.InfoAppText.setOnClickListener {
            findNavController().navigate(R.id.AboutApp)
        }
        this.binding.logoutText.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.initCurrentUser()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}