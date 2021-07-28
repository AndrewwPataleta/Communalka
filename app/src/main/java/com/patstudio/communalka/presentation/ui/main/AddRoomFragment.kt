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
import com.patstudio.communalka.databinding.FragmentAddRoomBinding
import com.patstudio.communalka.databinding.FragmentWelcomeBinding
import gone
import org.koin.android.viewmodel.ext.android.viewModel

class AddRoomFragment : Fragment() {

    private var _binding: FragmentAddRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AddRoomViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddRoomBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun initObservers() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}