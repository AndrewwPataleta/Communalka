package com.patstudio.communalka.presentation.ui.main.room

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patstudio.communalka.R
import com.patstudio.communalka.data.model.Placement
import com.patstudio.communalka.data.model.Room
import com.patstudio.communalka.data.model.UserForm
import com.patstudio.communalka.databinding.FragmentAddRoomBinding
import com.patstudio.communalka.databinding.FragmentPersonalAccountManagementBinding
import com.patstudio.communalka.presentation.ui.MainActivity
import gone
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import visible


class PersonalAccountManagementFragment : Fragment() {

   private var _binding: FragmentPersonalAccountManagementBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PersonalAccountManagementViewModel>()
    private lateinit var unconnectedPersonalAccountAdapter: UnconnectedPersonalAccountAdapter
    private lateinit var connectedPersonalAccountAdapter: ConnectedPersonalAccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPersonalAccountManagementBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun initObservers() {
        viewModel.getUnconnectedPersonalAccount().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {

                    unconnectedPersonalAccountAdapter = UnconnectedPersonalAccountAdapter(it, requireContext(), viewModel)
                    binding.unconnectedPersonalAccountList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.unconnectedPersonalAccountList.adapter = unconnectedPersonalAccountAdapter
                    binding.unconnectedPersonalAccountList.visible(false)
                    binding.unconnectedGroup.visible(false)
                }
            }
        }
        viewModel.getConnectedPersonalAccount().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {

                    connectedPersonalAccountAdapter = ConnectedPersonalAccountAdapter(it, requireContext(), viewModel)
                    binding.connectedPersonalAccountList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.connectedPersonalAccountList.adapter = connectedPersonalAccountAdapter

                    binding.connectedGroup.visible(false)
                }
            }
        }

        viewModel.getPersonalAccountForConnect().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("account" to it.first, "placement" to it.second)
                    if (it.first.account != null) {
                        findNavController().navigate(R.id.EditPersonalAccount, bundle)
                    } else {
                        findNavController().navigate(R.id.CreatePersonalAccount, bundle)
                    }

                }
            }
        }
        viewModel.subTitlePlacement.observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    (activity as MainActivity).toolbar.subtitle = it
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Placement>("placement")?.let {
            viewModel.setCurrentRoom(it)
        }
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}