package com.patstudio.communalka.presentation.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.databinding.FragmentWelcomeBinding
import gone
import org.koin.android.viewmodel.ext.android.viewModel

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<WelcomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        binding.loginOrRegistration.setOnClickListener {
            findNavController().navigate(R.id.loLoginPage)
        }

        return binding.root
    }

    private fun initObservers() {
        viewModel.getUser().observe(requireActivity()) {
            binding.loginOrRegistration.gone(false)
            binding.welcomeText.text = getString(R.string.welcome_user, it.name)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        arguments?.getParcelable<User>("user")?.let {
            viewModel.setCurrentUser(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}