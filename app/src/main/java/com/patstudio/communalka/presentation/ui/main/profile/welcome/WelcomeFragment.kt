package com.patstudio.communalka.presentation.ui.main.profile.welcome

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentWelcomeBinding
import com.patstudio.communalka.presentation.ui.main.WelcomeViewModel
import com.patstudio.communalka.presentation.ui.main.room.PlacementAdapter
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

class WelcomeFragment : Fragment() {

    private val REQUEST_READ_EXTERNAL: Int = 111
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
            val bundle = bundleOf("type" to "default")
            findNavController().navigate(R.id.loLoginPage, bundle)
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

                    this.binding.contentContainer.visible(true)
                    binding.login.gone(false)
                    binding.registration.gone(false)
                    val splitFio = it.name.split(" ")
                    var fio = ""
                    if (splitFio.size > 2) {
                        splitFio[1]?.let {
                            fio += it
                        }
                        splitFio[2]?.let {
                            fio += " " + it
                        }
                    } else {
                        splitFio[1]?.let {
                            fio += it
                        }
                    }

                    binding.welcomeText.text = getString(R.string.welcome_user, fio)

                }
            }
        }
        viewModel.getWithoutUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   this.binding.contentContainer.visible(true)
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
        viewModel.getReadStoragePermission().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_MEDIA_LOCATION,
                            ), REQUEST_READ_EXTERNAL
                        )
                    } else {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                            ), REQUEST_READ_EXTERNAL
                        )
                    }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_READ_EXTERNAL -> {
                viewModel.setReadStoragePermission(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}