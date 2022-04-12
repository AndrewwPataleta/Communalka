package com.communalka.app.presentation.ui.main.profile

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.communalka.app.R
import com.communalka.app.databinding.FragmentEditPhoneBinding
import org.koin.android.viewmodel.ext.android.viewModel
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.slots.PredefinedSlots
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class EditPhoneFragment : Fragment() {

    private var _binding: FragmentEditPhoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<EditPhoneViewModel>()
    val mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER)
    val watcher: FormatWatcher = MaskFormatWatcher(mask)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditPhoneBinding.inflate(inflater, container, false)
        viewModel.initCurrentUser()
        return binding.root
    }


    private fun initObservers() {

        watcher.installOn(binding.phoneValue)

        viewModel.getUser().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.currentPhoneValue.setText(it.phone)
                    binding.phoneValue.setText(it.phone)
                }
            }
        }

        viewModel.userPhoneError.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.phoneValue.error = it
                }
            }
        }
        viewModel.finish.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    findNavController().popBackStack()
                }
            }
        }
        viewModel.openConfirmCode.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = Bundle()
                    bundle.putString("phone", it)
                    findNavController().navigate(R.id.toEditPhoneConfirm, bundle)
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

        binding.phoneValue.addTextChangedListener {
            viewModel.setUserPhone(watcher.mask.toUnformattedString())
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