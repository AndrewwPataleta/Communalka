package com.communalka.app.presentation.ui.main.room

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.setPadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.communalka.app.R
import com.communalka.app.data.model.Placement
import com.communalka.app.databinding.FragmentEditRoomBinding
import gone
import org.koin.android.viewmodel.ext.android.sharedViewModel
import visible


class EditRoomFragment : Fragment() {

    private val IMAGE_PICK_CODE = 222
    private var _binding: FragmentEditRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<EditRoomViewModel>()
    lateinit var currentImage: ImageView
    private val REQUEST_READ_EXTERNAL = 111
    lateinit var res: Resources
    var value: Float = 0.0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEditRoomBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initObservers() {
        viewModel.getNameRoomError().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.roomNameEdit.setError(it)
                }
            }
        }
        viewModel.openPermissionSettings.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("Для доступа к фото измените разрешения")
                    builder.setPositiveButton("Изменить"){dialogInterface, which ->
                        Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${requireActivity().packageName}")).apply {
                            addCategory(Intent.CATEGORY_DEFAULT)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(this)
                        }
                        dialogInterface.dismiss()
                    }
                    builder.setNegativeButton("Отмена"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
            }
        }

        viewModel.getFioOwnerError().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.fioEdit.setError(it)
                }
            }
        }
        viewModel.getAddressError().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.addressEdit.setError(it)
                }
            }
        }
        viewModel.getOpenExternalPermission().observe(viewLifecycleOwner) {
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

        viewModel.getShowAddressLocation().observe(viewLifecycleOwner) {
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
        viewModel.getImages().observe(viewLifecycleOwner) {
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
        viewModel.getTotalSpaceError().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.totalAreaEdit.setError(it)
                }
            }
        }
        viewModel.getOpenListPlacements().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    findNavController().navigate(R.id.toWelcomePage)
                }
            }
        }
        viewModel.getShowProgress().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        binding.progress.visible(false)
                        binding.saveRoom.gone(false)
                    } else {
                        binding.progress.gone(false)
                        binding.saveRoom.visible(false)
                    }
                }
            }
        }
        viewModel.getDeleteDialog().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val builder = AlertDialog.Builder(requireContext())
                        .setTitle("Удалить помещение?")
                    builder.setMessage("Вы действительно хотите удалить помещение '"+it.name+"'")
                    builder.setPositiveButton("Удалить"){dialogInterface, which ->
                        viewModel.confirmRemoveRoom()
                        dialogInterface.dismiss()
                    }
                    builder.setNegativeButton("Отмена"){dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                }
            }
        }

        viewModel.getTotalLivingError().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.livingSpaceEdit.setError(it)
                }
            }
        }
        viewModel.getOpenRegistration().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        findNavController().navigate(R.id.toRegistration)
                    }
                }
            }
        }
        viewModel.getUserMessage().observe(viewLifecycleOwner) {
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

        viewModel.getOpenMainPage().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        findNavController().navigate(R.id.toWelcomePage)
                    }
                }
            }
        }
        viewModel.getListSuggestions().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {

                    val addresses:List<String> = it.second.map { it.value }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        com.communalka.app.R.layout.address_dropdown,
                        com.communalka.app.R.id.textview,
                        addresses
                    )

                    binding.addressEdit.setAdapter(adapter)
                    if (it.first) {
                        binding.addressEdit.showDropDown()
                    }

                }
            }
        }
        viewModel.getProgressSuggestions().observe(viewLifecycleOwner) {
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

        viewModel.getCheckExternalPermission().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_MEDIA_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
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

        viewModel.getStaticAddressImage().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val path = getString(R.string.static_address_url, it.first, it.second)
                    Glide.with(requireActivity()).load(path).into(binding.addressLocation);
                }
            }
        }

        viewModel.getProgressCreateRoom().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        binding.saveRoom.gone(false)
                        binding.progress.visible(false)
                    } else {
                        binding.saveRoom.visible(false)
                        binding.progress.gone(false)
                    }
                }
            }
        }

        viewModel.getImageURI().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.attachRoomImage.setPadding(0)
                    binding.attachRoomImage.setImageURI(it)
                }
            }
        }

        viewModel.getAvatarActionDialog().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val root = layoutInflater.inflate(R.layout.layout_action_avatar, null)
                    val actionPhotoDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                    actionPhotoDialog.setContentView(root)
                    root.findViewById<View>(R.id.removePhotoPlacement).setOnClickListener {
                        actionPhotoDialog.dismiss()
                        viewModel.removeAvatar()
                    }
                    root.findViewById<View>(R.id.changePhotoPlacement).setOnClickListener {
                        actionPhotoDialog.dismiss()
                        viewModel.changeAvatar()
                    }
                    actionPhotoDialog.show()
                }
            }
        }

        viewModel.getCurrentPlacement().observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    binding.model = it
                    binding.totalAreaEdit.setText(it.total_area.toString())
                    binding.livingSpaceEdit.setText(it.living_area.toString())

                    when (it.imageType) {
                        "DEFAULT" -> {
                            binding.attachRoomImage.setPadding(value.toInt())
                            when (it.path) {

                                "HOME" -> {
                                    binding.attachRoomImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_home
                                        )
                                    )
                                }
                                "ROOM" -> {
                                    binding.attachRoomImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_room
                                        )
                                    )
                                }
                                "OFFICE" -> {
                                    binding.attachRoomImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_office
                                        )
                                    )
                                }
                                "HOUSE" -> {
                                    binding.attachRoomImage.setImageDrawable(
                                        binding.root.context.resources.getDrawable(
                                            R.drawable.ic_country_house
                                        )
                                    )
                                }
                            }
                        }
                        "STORAGE" -> {
                            binding.attachRoomImage.setPadding(0)
                            binding.attachRoomImage.setImageURI(it.path.toUri())
                        }
                    }

                }
            }
        }
    }

    private fun initBindingListeners() {
        binding.roomNameEdit.doAfterTextChanged {
            viewModel.setRoomName(it.toString())
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
        binding.addressEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (binding.addressEdit.isPerformingCompletion()) {

                } else {
                    viewModel.setAddressName(charSequence.toString())
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })

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
        arguments?.getParcelable<Placement>("placement")?.let {
            viewModel.setCurrentPlacement(it)
        }

        initObservers()
        initBindingListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}