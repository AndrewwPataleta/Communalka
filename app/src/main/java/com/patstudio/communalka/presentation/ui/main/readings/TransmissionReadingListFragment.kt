package com.patstudio.communalka.presentation.ui.main.readings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.databinding.FragmentTransmissionReadingListBinding
import com.patstudio.communalka.databinding.FragmentTransmissionReadingsBinding
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class TransmissionReadingListFragment : Fragment() {

   private var _binding: FragmentTransmissionReadingListBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<TransmissionReadingListViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTransmissionReadingListBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initObservers() {
        viewModel.currentPlacement.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.model = it
                    when (it.imageType) {
                        "DEFAULT" -> {
                            when (it.path) {
                                "HOME" -> {
                                    binding.placementImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_home
                                        )
                                    )
                                }
                                "ROOM" -> {
                                    binding.placementImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_room
                                        )
                                    )
                                }
                                "OFFICE" -> {
                                    binding.placementImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_office
                                        )
                                    )
                                }
                                "HOUSE" -> {
                                    binding.placementImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_country_house
                                        )
                                    )
                                }
                            }
                        }
                        "STORAGE" -> {
                            binding.placementImage.setPadding(0)
                            binding.placementImage.setImageURI(it.path.toUri())
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.counterContainer.root.setOnClickListener {
            findNavController().navigate(R.id.toTransmissionReadingCounter)
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