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
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentAddRoomBinding
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible


class AddRoomFragment : Fragment() {

    private val IMAGE_PICK_CODE = 222
    private var _binding: FragmentAddRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<AddRoomViewModel>()
    lateinit var currentImage: ImageView
    private val REQUEST_READ_EXTERNAL = 111
    lateinit var res: Resources
    var value: Float = 0.0f


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
        viewModel.getOpenExternalPermission().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                  if (it) {
                      val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                      intent.type = "image/*"
                      intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                      intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                      startActivityForResult(intent, IMAGE_PICK_CODE)
                  }
                }
            }
        }

        viewModel.getShowAddressLocation().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                       binding.addressLocation.visible(false)
                    } else {
                        binding.addressLocation.gone(false)
                    }
                }
            }
        }
        viewModel.getImages().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    it.forEach {
                        when (it.key) {
                            1 -> {
                                currentImage = binding.attachRoomImage
                            }
                            2 -> {
                                currentImage = binding.secondAddRoomIcon
                            }
                            3 -> {
                                currentImage = binding.thirdAddRoomIcon
                            }
                            4 -> {
                                currentImage = binding.fourAddRoomIcon
                            }
                        }
                        binding.attachRoomImage.setPadding(value.toInt())
                        when (it.value) {
                            "HOME" -> {
                                currentImage.setImageDrawable(resources.getDrawable(R.drawable.ic_home))
                            }
                            "ROOM" -> {
                                currentImage.setImageDrawable(resources.getDrawable(R.drawable.ic_room))
                            }
                            "OFFICE" -> {
                                currentImage.setImageDrawable(resources.getDrawable(R.drawable.ic_office))
                            }
                            "HOUSE" -> {
                                currentImage.setImageDrawable(resources.getDrawable(R.drawable.ic_country_house))
                            }
                        }
                    }
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
        viewModel.getOpenRegistration().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        findNavController().navigate(R.id.toRegistration)
                    }
                }
            }
        }
        viewModel.getUserMessage().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val builder = MaterialAlertDialogBuilder(requireContext())
                    builder.setTitle(it)
                    builder.setPositiveButton("Ok"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    builder.setCancelable(true)
                    builder.show()
                }
            }
        }

        viewModel.getOpenMainPage().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        val bundle = bundleOf("need_login" to false)
                        findNavController().navigate(R.id.toWelcomePage, bundle)
                    }
                }
            }
        }
        viewModel.getListSuggestions().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {

                    val addresses:List<String> = it.map { it.value }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.spinner_dropdown_item,
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

        viewModel.getStaticAddressImage().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val path = getString(R.string.static_address_url, it.first, it.second)
                    Log.d("AddRoomFragment", "path "+path)
                    Glide.with(requireActivity()).load(path).into(binding.addressLocation);
                }
            }
        }

        viewModel.getProgressCreateRoom().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    Log.d("AddRoomFragment", "progress is "+it)
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

        viewModel.getImageURI().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.attachRoomImage.setPadding(0)
                    binding.attachRoomImage.setImageURI(it)
                    Log.d("AddRoomFragment", "URI "+it.toString())
                  //  Glide.with(requireActivity()).load(it).into(binding.attachRoomImage);
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
        binding.attachRoomImage.setOnClickListener {
            viewModel.selectFirstImage()
        }
        binding.iconAdd.setOnClickListener {
            viewModel.selectFirstImage()
        }

        binding.secondAddRoomIcon.setOnClickListener {
            viewModel.selectSecondImage()
        }
        binding.thirdAddRoomIcon.setOnClickListener {
            viewModel.selectThirdImage()
        }
        binding.fourAddRoomIcon.setOnClickListener {
            viewModel.selectFourImage()
        }
        binding.saveRoom.setOnClickListener {
            viewModel.saveRoom()
        }
        binding.addressEdit.setOnItemClickListener(OnItemClickListener { parent, arg1, pos, id ->
           viewModel.selectSuggest(pos)
        })
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
                    viewModel.setCurrentRoomImage(it)
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        res = resources
        value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28F, res.getDisplayMetrics())
        viewModel.initApiKey()
        initObservers()
        initBindingListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}