package com.communalka.app.presentation.ui.main.room

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.communalka.app.R
import com.communalka.app.data.model.Service
import com.communalka.app.data.model.Placement
import com.communalka.app.databinding.FragmentCreatePersonalAccountBinding
import com.communalka.app.presentation.ui.splash.MainViewModel
import com.communalka.app.common.utils.gone
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import com.communalka.app.common.utils.visible


class CreatePersonalAccountFragment : Fragment() {

   private var _binding: FragmentCreatePersonalAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<CreatePersonalAccountViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()
    private lateinit var personalCounterAdapter: PersonalCounterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCreatePersonalAccountBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun initObservers() {
        viewModel.getPersonalAccount().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        mainViewModel.currentPersonalAccountName(it.name)
                    }
                }
            }
        }

        viewModel.getSupplierList().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        val adapter = ArrayAdapter(requireContext(),
                            R.layout.spinner_dropdown_item, R.id.nameSupplier, it.map { it.name })
                        binding.serviceProviderValue.adapter = adapter
                    }
                }
            }
        }

        viewModel.getPersonalCounters().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        val adapter = PersonalCounterAdapter(it,requireActivity(), viewModel)
                        binding.personalCountersContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                        binding.personalCountersContainer.adapter = adapter
                    }
                }
            }
        }

        viewModel.userMessage.observe(viewLifecycleOwner) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(it)
            builder.setPositiveButton("Ок"){dialogInterface, which ->
                dialogInterface.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        viewModel.getProgressConnectPersonalNumber().observe(this) {
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
        viewModel.getPersonalNumberError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                       binding.personalNumberValue.setError(it.toString())
                    }
                }
            }
        }

        viewModel.disableCreate.observe(viewLifecycleOwner) {
            binding.connectPersonalAccount.isEnabled = !it
        }
        viewModel.getOpenPersonalAccountsPage().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it?.let {
                        it.second?.let {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("Услуга '${it}' добавлена")
                            builder.setMessage("Номер лицевого счета отправлен на подтверждение поставщику. \n" +
                                    "\n" +
                                    "Время подтверждения от 2 часов до 3 дней.")
                            builder.setPositiveButton("Ок"){dialogInterface, which ->
                                dialogInterface.dismiss()
                            }
                            val alertDialog: AlertDialog = builder.create()
                            alertDialog.setCancelable(false)
                            alertDialog.show()
                        }


                        val bundle = bundleOf("placement" to it.first)
                        findNavController().navigate(R.id.toPersonalAccounts, bundle)
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.serviceProviderValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setSelectedPosition(position)
            }

        }
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