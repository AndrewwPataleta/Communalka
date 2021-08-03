package com.patstudio.communalka.presentation.ui.main.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.patstudio.communalka.databinding.FragmentAddRoomBinding
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible


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
        viewModel.getNameRoomError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.roomNameEdit.setError(it)
                }
            }
        }
        viewModel.getFioOwnerError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.fioEdit.setError(it)
                }
            }
        }
        viewModel.getAddressError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.addressEdit.setError(it)
                }
            }
        }
        viewModel.getTotalSpaceError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.totalAreaEdit.setError(it)
                }
            }
        }
        viewModel.getTotalLivingError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.livingSpaceEdit.setError(it)
                }
            }
        }
        viewModel.getListSuggestions().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {

                    val addresses:List<String> = it.map { it.value }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        addresses
                    )

                    binding.addressEdit.setAdapter(adapter)
                    binding.addressEdit.showDropDown()
                }
            }
        }
        viewModel.getProgressSuggestions().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                     if (it) {
                         binding.progressSuggestions.visible(true)
                         binding.progressSuggestions.visible(true)
                     } else {
                         binding.progressSuggestions.gone(true)
                     }
                }
            }
        }

        viewModel.getProgressCreateRoom().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        binding.saveRoom.gone(false)
                        binding.progressCreate.visible(false)
                    } else {
                        binding.saveRoom.visible(false)
                        binding.progressCreate.gone(false)
                    }
                }
            }
        }
    }

    private fun initBindingListeners() {
        binding.roomNameEdit.doAfterTextChanged {
            viewModel.setRoomName(it.toString())
        }
        binding.addressEdit.doAfterTextChanged {
            viewModel.setAddressName(it.toString())
        }
        binding.fioEdit.doAfterTextChanged {
            viewModel.setFioOwner(it.toString())
        }
        binding.totalAreaEdit.doAfterTextChanged {
            viewModel.setTotalSpace(it.toString())
        }
        binding.livingSpaceEdit.doAfterTextChanged {
            viewModel.setLivingSpace(it.toString())
        }
        binding.saveRoom.setOnClickListener {
            viewModel.saveRoom()
        }
        binding.addressEdit.setOnItemClickListener(OnItemClickListener { parent, arg1, pos, id ->
           viewModel.selectSuggest(pos)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initApiKey()
        initObservers()
        initBindingListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}