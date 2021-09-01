package com.patstudio.communalka.presentation.ui.main.profile.security

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentEmailEditBinding
import com.patstudio.communalka.databinding.FragmentPersonalInfoBinding
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

class EmailEditFragment : Fragment() {

    private var _binding: FragmentEmailEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<EmailEditViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEmailEditBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun initObservers() {
        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    this.binding.emailCurrentValue.setText(it.email)
                }
            }
        }
        viewModel.getEmailError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    this.binding.emailCurrentValue.setError(it)
                }
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

    }

    private fun initListeners() {
        binding.editUserBtn.setOnClickListener {
            viewModel.editUser()
        }
        binding.emailValue.doAfterTextChanged {
            viewModel.setUserEmail(it.toString())
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