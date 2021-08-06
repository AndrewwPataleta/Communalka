package com.patstudio.communalka.presentation.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.User
import com.patstudio.communalka.databinding.FragmentWelcomeBinding
import com.patstudio.communalka.presentation.ui.main.room.PlacementAdapter
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<WelcomeViewModel>()
    private lateinit var placementAdapter: PlacementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        binding.login.setOnClickListener {
            findNavController().navigate(R.id.loLoginPage)
        }
        binding.registration.setOnClickListener {
            findNavController().navigate(R.id.toRegistration)
        }
        binding.addNewPremises.setOnClickListener {
            viewModel.checkAvailableToOpenAddRoom()
        }
        binding.addRoom.setOnClickListener {
            viewModel.checkAvailableToOpenAddRoom()
        }
        return binding.root
    }


    private fun initObservers() {
        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.login.gone(false)
                    binding.registration.gone(false)
                    binding.welcomeText.text = getString(R.string.welcome_user, it.name)
                }
            }
        }
        viewModel.getNavigateTo().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   when (it) {
                       "ADD_ROOM" ->  {
                           findNavController().navigate(R.id.action_WelcomeFragment_to_AddRoom)
                       }
                       "REGISTRATION" ->  {
                           findNavController().navigate(R.id.toRegistration)
                       }
                   }
                }

            }
        }
        viewModel.getPinForm().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    Log.d("WelcomeFragment", "open pin form")
                    val bundle = bundleOf("user" to it)
                    findNavController().navigate(R.id.toPinCode, bundle)
                }

            }
        }
        viewModel.getPlacementList().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    requireActivity().findViewById<Toolbar>(R.id.toolbar).visible(false)
                    Log.d("WelcomeFragment", it.toString())
                    binding.userContainer.gone(false)
                    binding.premisesContainer.visible(false)
                    val adapter = PlacementAdapter(it)
                    binding.premisesList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.premisesList.adapter = adapter
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        val needLogin = arguments?.getBoolean("need_login", true)
        Log.d("WelcomeFragment", needLogin.toString())
        if (needLogin != null) {
            viewModel.setNeedEnterPin(needLogin)
            viewModel.initCurrentUser()
        } else {
            viewModel.initCurrentUser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}