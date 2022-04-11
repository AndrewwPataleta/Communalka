package com.communalka.app.presentation.ui.main.profile.security

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.communalka.app.R
import com.communalka.app.databinding.FragmentEmailEditBinding
import org.koin.android.viewmodel.ext.android.viewModel

class EmailEditFragment : Fragment() {

    private var _binding: FragmentEmailEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<EmailEditViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEmailEditBinding.inflate(inflater, container, false)
        viewModel.initCurrentUser()
        return binding.root
    }


    private fun initObservers() {


        viewModel.getUser().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.model = it
                    binding.emailValue.setText(it.email)
                }
            }
        }

        viewModel.userEmailError.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.emailValue.error = it
                }
            }
        }
        viewModel.finish.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    requireActivity().onBackPressed()
                }
            }
        }
        viewModel.openConfirmCode.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    var bundle = Bundle()
                    bundle.putString("email", it)
                    findNavController().navigate(R.id.EditEmailConfirm, bundle)
                }
            }
        }
        viewModel.getUserMessage().observe(viewLifecycleOwner) {
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

        binding.emailValue.addTextChangedListener {
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