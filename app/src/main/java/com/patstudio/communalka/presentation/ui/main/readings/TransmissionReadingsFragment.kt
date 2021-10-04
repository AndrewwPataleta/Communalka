package com.patstudio.communalka.presentation.ui.main.readings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.PlacementMeter
import com.patstudio.communalka.databinding.FragmentTransmissionReadingsBinding
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import gone
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import visible


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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListeners() {
        editTextList.add(binding.firstNumber)
        editTextList.add(binding.secondNumber)
        editTextList.add(binding.thirdNumber)
        editTextList.add(binding.fourNumber)
        editTextList.add(binding.fiveNumber)
//        editTextList.add(binding.sixNumber)
//        editTextList.add(binding.sevenNumber)
//        editTextList.add(binding.eigthNumber)

        binding.sendTransmissions.setOnClickListener {
            var transmission = binding.firstNumber.text.toString()+binding.secondNumber.text.toString()+binding.thirdNumber.text.toString()+binding.fourNumber.text.toString()+binding.fiveNumber.text.toString()
            if (!binding.sixNumber.text.toString().isNullOrEmpty() || !binding.sevenNumber.text.toString().isNullOrEmpty() || !binding.eigthNumber.text.toString().isNullOrEmpty()) {
                transmission += ","+binding.sixNumber.text.toString()+binding.sevenNumber.text.toString()+binding.eigthNumber.text.toString()
            }

            viewModel.sendTransmissions(transmission)
        }

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

        binding.firstNumber.doAfterTextChanged {
           it?.let {
               if (it.length == 1) binding.secondNumber.requestFocus()
           }
        }
        binding.secondNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.thirdNumber.requestFocus()
            }
        }
        binding.thirdNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.fourNumber.requestFocus()
            }
        }
        binding.fourNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.fiveNumber.requestFocus()
            }
        }
        binding.fiveNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.sixNumber.requestFocus()
            }
        }
//        binding.sixNumber.doAfterTextChanged {
//            it?.let {
//                if (it.length == 1) binding.sevenNumber.requestFocus()
//            }
//        }
//        binding.sevenNumber.doAfterTextChanged {
//            it?.let {
//                if (it.length == 1) binding.eigthNumber.requestFocus()
//
//            }
//        }
    }

    private fun clearValueCurrentFocus() {
        editTextList.map {
            if (it.hasFocus()) {
                it.setText("value")
            }
        }
    }

    private fun setValueToCurrentFocus(value: String) {
        editTextList.map {
            if (it.hasFocus()) {
                it.setText(value)
            }
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