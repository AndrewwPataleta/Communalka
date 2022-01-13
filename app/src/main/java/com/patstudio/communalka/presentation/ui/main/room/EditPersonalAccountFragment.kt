package com.patstudio.communalka.presentation.ui.main.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Service
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.FragmentEditPersonalAccountBinding
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import gone
import org.koin.android.viewmodel.ext.android.sharedViewModel
import visible


class EditPersonalAccountFragment : Fragment() {

   private var _binding: FragmentEditPersonalAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<EditPersonalAccountViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()
    private lateinit var personalCounterAdapter: EditPersonalCounterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditPersonalAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initObservers() {
        viewModel.getPersonalAccount().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        mainViewModel.currentPersonalAccountName(it.name)
                        binding.personalNumberValue.setText(it.account.number)
                    }
                }
            }
        }
        viewModel.supplierName.observe(viewLifecycleOwner) {
            binding.serviceProviderValue.setText(it)
        }

        viewModel.getRemovePersonalAccountDialog().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        val builder = MaterialAlertDialogBuilder(requireContext())
                        builder.setTitle("Удалить личный счет?")
                        builder.setMessage("Вы действительно хотите удалить л/c '"+it.name+"'?")
                        builder.setPositiveButton("Удалить"){dialogInterface, which ->
                            viewModel.confirmRemovePersonalAccount()
                            dialogInterface.dismiss()
                        }
                        builder.setNegativeButton("Отмена"){dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        builder.setCancelable(false)
                        builder.show()
                    }
                }
            }
        }

        viewModel.getOpenPersonalAccountsPage().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        val bundle = bundleOf("placement" to it)
                        findNavController().navigate(R.id.toPersonalAccounts, bundle)
                    }
                }
            }
        }

        viewModel.getPersonalCounters().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        val adapter = EditPersonalCounterAdapter(it,requireActivity(), viewModel)
                        binding.personalCountersContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                        binding.personalCountersContainer.adapter = adapter
                    }
                }
            }
        }
        viewModel.getProgressConnectPersonalNumber().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                       if (it) {
                           binding.progress.visible(false)
                           binding.connectPersonalAccount.gone(false)
                       } else {
                           binding.progress.gone(false)
                           binding.connectPersonalAccount.visible(false)
                       }
                    }
                }
            }
        }
        viewModel.getPersonalNumberError().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                       binding.personalNumberValue.setError(it.toString())
                    }
                }
            }
        }
    }

    private fun initListeners() {

        binding.addCounter.setOnClickListener {
            viewModel.addNewCounter()
        }
        binding.connectPersonalAccount.setOnClickListener{
            viewModel.connectPersonalNumber()
        }
        binding.personalNumberValue.doAfterTextChanged{
            viewModel.setPersonalNumber(it.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Service>("account")?.let {
            viewModel.setPersonalAccount(it)
        }
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