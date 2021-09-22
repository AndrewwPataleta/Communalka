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
import com.patstudio.communalka.databinding.FragmentTransmissionReadingsBinding
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel


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

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListeners() {
        editTextList.add(binding.firstNumber)
        editTextList.add(binding.secondNumber)
        editTextList.add(binding.thirdNumber)
        editTextList.add(binding.fourNumber)
        editTextList.add(binding.fiveNumber)
        editTextList.add(binding.sixNumber)
        editTextList.add(binding.sevenNumber)
        editTextList.add(binding.eigthNumber)

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
        binding.sixNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.sevenNumber.requestFocus()
            }
        }
        binding.sevenNumber.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.eigthNumber.requestFocus()

            }
        }

//        binding.pinOne.doAfterTextChanged {
//            setValueToCurrentFocus("1")
//        }
//
//        binding.pinTwo.doAfterTextChanged {
//            setValueToCurrentFocus("2")
//        }
//        binding.pinTree.doAfterTextChanged {
//            setValueToCurrentFocus("3")
//        }
//        binding.pinFour.doAfterTextChanged {
//            setValueToCurrentFocus("4")
//        }
//        binding.pinFive.doAfterTextChanged {
//            setValueToCurrentFocus("5")
//        }
//        binding.pinSix.doAfterTextChanged {
//            setValueToCurrentFocus("6")
//        }
//        binding.pinSeven.doAfterTextChanged {
//            setValueToCurrentFocus("7")
//        }
//        binding.pinEight.doAfterTextChanged {
//            setValueToCurrentFocus("8")
//        }
//        binding.pinNine.doAfterTextChanged {
//            setValueToCurrentFocus("9")
//        }
//        binding.pinZero.doAfterTextChanged {
//            setValueToCurrentFocus("0")
//        }
//        binding.pinBack.doAfterTextChanged {
//            clearValueCurrentFocus()
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
        arguments?.getParcelable<Placement>("placement")?.let {
            viewModel.setCurrentPlacement(it)
        }
        initObservers()
        initListeners()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}