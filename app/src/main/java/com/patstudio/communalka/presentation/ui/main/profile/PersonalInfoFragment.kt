package com.patstudio.communalka.presentation.ui.main.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentPersonalInfoBinding
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible

class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PersonalInfoViewModel>()
    private val REQUEST_READ_EXTERNAL: Int = 111
    private val IMAGE_PICK_CODE = 222

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun initObservers() {
        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    this.binding.fioEdit.setText(it.name)
                    if (it.photoPath.length > 0) {
                        binding.mockAvatar.gone(false)
                        binding.attachRoomImage.setImageURI(Uri.parse(it.photoPath))
                    }
                }
            }
        }
        viewModel.getCheckExternalPermission().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_MEDIA_LOCATION,
                            ), REQUEST_READ_EXTERNAL
                        )
                    } else {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                            ), REQUEST_READ_EXTERNAL
                        )
                    }
                }
            }
        }
        viewModel.getOpenExternalPermission().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                        intent.type = "image/*"
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        startActivityForResult(intent, IMAGE_PICK_CODE)
                    }
                }
            }
        }
        viewModel.getImageURI().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.mockAvatar.gone(false)
                    binding.attachRoomImage.setImageURI(it)
                }
            }
        }
        viewModel.getUserMessage().observe(this) {
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

        viewModel.getUserFioError().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.fioEdit.setError(it)
                }
            }
        }
    }

    private fun initListeners() {
        binding.attachRoomImage.setOnClickListener {
            viewModel.changeUserAvatar()
        }
        binding.editUserBtn.setOnClickListener {
            viewModel.editUser()
        }
        binding.fioEdit.doAfterTextChanged {
            viewModel.setUserFio(it.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_READ_EXTERNAL -> {
                viewModel.haveReadExternalPermission(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            data?.data?.let {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                it?.let {
                    requireContext().contentResolver.takePersistableUriPermission(it, takeFlags)
                    viewModel.setUserAvatar(it)
                }

            }
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