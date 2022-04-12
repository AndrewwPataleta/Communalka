package com.communalka.app.presentation.ui.main.readings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.communalka.app.R
import com.communalka.app.data.model.PlacementMeter
import com.communalka.app.databinding.FragmentTransmissionReadingsBinding
import com.communalka.app.presentation.ui.splash.MainViewModel
import com.communalka.app.common.utils.gone
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import com.communalka.app.common.utils.visible


class TransmissionReadingsFragment : Fragment() {

   private var _binding: FragmentTransmissionReadingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<TransmissionReadingsViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()
    private var editTextList: MutableList<EditText> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTransmissionReadingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initObservers() {
        viewModel.currentPlacement.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.model = it
                }
            }
        }
        viewModel.prevScreen.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    requireActivity().onBackPressed()
                }
            }
        }
        viewModel.userMessage.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val builder = MaterialAlertDialogBuilder(requireContext())

                    builder.setMessage(it)
                    builder.setPositiveButton("Ок"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    builder.setCancelable(false)
                    builder.show()
                }
            }
        }
        viewModel.isSendingTransmissions.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                  if (it) {
                      binding.progressSendTransmissions.visible(false)
                      binding.sendTransmissions.gone(false)
                  } else {
                      binding.progressSendTransmissions.gone(false)
                      binding.sendTransmissions.visible(false)
                  }
                }
            }
        }
    }

    private fun generateTransmission(): String {
        var transmission = binding.firstNumber.text.toString() + binding.secondNumber.text.toString() + binding.thirdNumber.text.toString() + binding.fourNumber.text.toString() + binding.fiveNumber.text.toString()
        if (transmission.isNullOrEmpty()) {
            disableTransmission()
        } else {
            enableTransmission()
        }
        return transmission
    }

    private fun enableTransmission() {
        binding.sendTransmissions.background = resources.getDrawable(R.drawable.background_rounded_blue)
        binding.sendTransmissions.setOnClickListener {
            viewModel.sendTransmissions(generateTransmission())
        }
    }

    private fun disableTransmission() {

        binding.sendTransmissions.background = resources.getDrawable(R.drawable.gray_button_disable_background)
        binding.sendTransmissions.setOnClickListener {

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListeners() {
        editTextList.add(binding.firstNumber)
        editTextList.add(binding.secondNumber)
        editTextList.add(binding.thirdNumber)
        editTextList.add(binding.fourNumber)
        editTextList.add(binding.fiveNumber)
        binding.firstNumber.setShowSoftInputOnFocus(false);
        binding.secondNumber.setShowSoftInputOnFocus(false);
        binding.thirdNumber.setShowSoftInputOnFocus(false);
        binding.fourNumber.setShowSoftInputOnFocus(false);
        binding.fiveNumber.setShowSoftInputOnFocus(false);





        disableTransmission()

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

//        binding.firstNumber.setOnFocusChangeListener { view, b ->
//            if (b) {
//                binding.firstNumber.background =
//                    resources.getDrawable(R.drawable.rounded_blue_background)
//            } else {
//                binding.firstNumber.background =
//                    resources.getDrawable(R.drawable.light_gray_button_disable_background)
//            }
//        }
//
//        binding.secondNumber.setOnFocusChangeListener { view, b ->
//            if (b) {
//                binding.secondNumber.background =
//                    resources.getDrawable(R.drawable.rounded_blue_background)
//            } else {
//                binding.secondNumber.background =
//                    resources.getDrawable(R.drawable.light_gray_button_disable_background)
//            }
//        }
        binding.firstNumber.requestFocus()
        binding.firstNumber.doAfterTextChanged {
           it?.let {
               if (it.length == 1) {
                   binding.secondNumber.requestFocus()
               }
               generateTransmission()
           }
        }
        binding.secondNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.thirdNumber.requestFocus()
                generateTransmission()
            }
            if (it.isNullOrEmpty()) {
                binding.firstNumber.requestFocus()
            }
        }
        binding.thirdNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.fourNumber.requestFocus()
                else if (it.isEmpty()) binding.secondNumber.requestFocus()
                generateTransmission()
            }
            if (it.isNullOrEmpty()) {
                binding.secondNumber.requestFocus()
            }
        }
        binding.fourNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.fiveNumber.requestFocus()
                else if (it.isEmpty()) binding.thirdNumber.requestFocus()
                generateTransmission()
            }
            if (it.isNullOrEmpty()) {
                binding.thirdNumber.requestFocus()
            }
        }

        binding.fiveNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.sixNumber.requestFocus()
                else if (it.isEmpty()) binding.fourNumber.requestFocus()
                generateTransmission()
            }
        }

        binding.pinOne.setOnClickListener { setValueToCurrentFocus((getString(R.string.one))) }
        binding.pinTwo.setOnClickListener { setValueToCurrentFocus((getString(R.string.two))) }
        binding.pinTree.setOnClickListener { setValueToCurrentFocus((getString(R.string.tree))) }
        binding.pinFour.setOnClickListener { setValueToCurrentFocus((getString(R.string.four))) }
        binding.pinFive.setOnClickListener {setValueToCurrentFocus((getString(R.string.five))) }
        binding.pinSix.setOnClickListener { setValueToCurrentFocus((getString(R.string.six))) }
        binding.pinSeven.setOnClickListener {setValueToCurrentFocus((getString(R.string.seven)))}
        binding.pinEight.setOnClickListener {setValueToCurrentFocus((getString(R.string.eigth))) }
        binding.pinNine.setOnClickListener {setValueToCurrentFocus((getString(R.string.nine))) }
        binding.pinZero.setOnClickListener { setValueToCurrentFocus((getString(R.string.zero))) }
        binding.pinBack.setOnClickListener { clearValueCurrentFocus() }
    }

    private fun clearValueCurrentFocus() {
        editTextList.map {
            if (it.hasFocus() ) {

                it.setText("")
            }
        }
    }

    private fun setValueToCurrentFocus(value: String) {
        var editText: EditText = EditText(requireContext())
        editTextList.map {
            if (it.hasFocus()) {
                editText = it
            }
        }
        editText?.let {
            it.setText(value)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<PlacementMeter>("meter")?.let {
            viewModel.setCurrentMeter(it)
        }
        initObservers()
        initListeners()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}