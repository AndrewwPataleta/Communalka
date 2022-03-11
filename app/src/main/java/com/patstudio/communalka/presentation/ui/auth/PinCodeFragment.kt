package com.patstudio.communalka.presentation.ui.auth

import android.app.AlertDialog
import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.databinding.FragmentPinCodeBinding
import com.patstudio.communalka.presentation.ui.MainActivity
import com.patstudio.communalka.presentation.ui.main.profile.welcome.WelcomeViewModel
import gone
import invisible
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import visible
import java.util.concurrent.Executor

class PinCodeFragment : Fragment() {

    private var _binding: FragmentPinCodeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PinCodeViewModel>()
    private val welcomeViewModel by sharedViewModel<WelcomeViewModel>()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPinCodeBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun initObservers() {
       viewModel.getPinCode().observe(this) {
           Log.d("PinCodeFragment", "pin "+it)
           when (it.length) {

               0 -> {
                   binding.firstPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
                   binding.secondPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
                   binding.thirdPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
                   binding.fourthPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
               }
               1 -> {
                   binding.firstPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.secondPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
                   binding.thirdPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
                   binding.fourthPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
               }
               2 -> {
                   binding.firstPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.secondPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.thirdPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
                   binding.fourthPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
               }
               3 -> {
                   binding.firstPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.secondPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.thirdPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.fourthPinElement.background = resources.getDrawable(R.drawable.ic_pin_empty)
               }
               4 -> {
                   binding.firstPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.secondPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.thirdPinElement.background = resources.getDrawable(R.drawable.ic_pin)
                   binding.fourthPinElement.background = resources.getDrawable(R.drawable.ic_pin)
               }
           }
       }
        viewModel.getAvailableFingerPrint().observe(this) {
            try {
                if (it) {
                    binding.pinFingerprint.visible(false)
                    binding.pinFingerprint.callOnClick()
                } else {
                    binding.pinFingerprint.invisible(false)
                }
            } catch (e: Exception) {}
        }

        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    welcomeViewModel.setNeedEnterPin(false)
                    findNavController().navigate(R.id.WelcomeFragment)
                }
            }
        }



        viewModel.getAlertMessage().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle(it)
                    builder.setPositiveButton("Ок") { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
            }
        }

        viewModel.getAccessBack().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
//                   if (it) {
//                       binding.close.visible(false)
//                   } else {
//                       binding.close.invisible(false)
//
//                   }
                }
            }
        }

        viewModel.getPinCodeMode().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    try {
                        when (it) {
                            "INSTALL" -> {
                                binding.pinCodeText.text =
                                    getString(R.string.install_pin_code_short)
                                binding.installPinCodeLong.text =
                                    getString(R.string.install_pin_code_long)
                                binding.forgotPassword.gone(false)
                            }
                            "REPEAT" -> {
                                binding.pinCodeText.text = getString(R.string.repeat_pin_code_short)
                                binding.installPinCodeLong.text =
                                    getString(R.string.install_pin_code_long)
                                binding.forgotPassword.gone(false)
                            }
                            "AUTH" -> {
                                binding.pinCodeText.text = getString(R.string.enter_pin_code)
                                binding.installPinCodeLong.text =
                                    getString(R.string.enter_pin_code_second)
                                binding.forgotPassword.visible(true)
                            }
                        }
                    } catch (e: Exception) {}
                }
            }
        }
    }



    private fun initListeners() {
        binding.pinOne.setOnClickListener { viewModel.clickDigital(getString(R.string.one)) }
        binding.pinTwo.setOnClickListener { viewModel.clickDigital(getString(R.string.two)) }
        binding.pinTree.setOnClickListener { viewModel.clickDigital(getString(R.string.tree)) }
        binding.pinFour.setOnClickListener { viewModel.clickDigital(getString(R.string.four)) }
        binding.pinFive.setOnClickListener { viewModel.clickDigital(getString(R.string.five)) }
        binding.pinSix.setOnClickListener { viewModel.clickDigital(getString(R.string.six)) }
        binding.pinSeven.setOnClickListener { viewModel.clickDigital(getString(R.string.seven)) }
        binding.pinEight.setOnClickListener { viewModel.clickDigital(getString(R.string.eigth)) }
        binding.pinNine.setOnClickListener { viewModel.clickDigital(getString(R.string.nine)) }
        binding.pinZero.setOnClickListener { viewModel.clickDigital(getString(R.string.zero)) }
        binding.pinBack.setOnClickListener { viewModel.removeLastItem() }
        binding.close.setOnClickListener {
            startActivity(Intent(requireContext(),MainActivity::class.java))
        }
        binding.forgotPassword.setOnClickListener {  findNavController().navigate(R.id.toRestore) }

        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    viewModel.fingerPrintError()
                    Toast.makeText(requireContext(),
                        "Ошибка авторизации: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.fingerPrintSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.auth_with_finger_print))
            .setNegativeButtonText(getString(R.string.user_pin_code))
            .build()

        binding.pinFingerprint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun initNavigationListeners() {

    }

    private fun disableNavigationListeners() {

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        arguments?.getParcelable<UserForm>("user")?.let {
            viewModel.setUserForm(it)
        }

        initListeners()
        initNavigationListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}