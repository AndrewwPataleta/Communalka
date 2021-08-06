package com.patstudio.communalka.presentation.ui.main.room

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
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
                      val intent = Intent(Intent.ACTION_PICK)
                      intent.type = "image/*"
                      startActivityForResult(intent, IMAGE_PICK_CODE)
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

        viewModel.getCheckExternalPermission().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                        ), REQUEST_READ_EXTERNAL
                    )
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
                viewModel.setCurrentRoomImage(it)
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