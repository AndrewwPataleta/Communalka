package com.patstudio.communalka.presentation.ui.main.profile.welcome

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentWelcomeBinding
import com.patstudio.communalka.presentation.ui.main.room.PlacementAdapter
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import visible
import com.skydoves.balloon.*
import com.skydoves.balloon.extensions.dp
import gone


class WelcomeFragment : Fragment() {

    private val REQUEST_READ_EXTERNAL: Int = 111
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<WelcomeViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()
    private lateinit var placementAdapter: PlacementAdapter
    private lateinit var balloonTransmit: Balloon
    private lateinit var balloonPayment: Balloon
    private lateinit var transmitAnchor: View
    private lateinit var paymentAnchor: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        binding.login.setOnClickListener {
            val bundle = bundleOf("type" to "default")
            findNavController().navigate(R.id.loLoginPage, bundle)
        }
        binding.registration.setOnClickListener {
            findNavController().navigate(R.id.toRegistration)
        }
        binding.addNewPremises.setOnClickListener {
            viewModel.checkAvailableToOpenAddRoom()
        }
        binding.addRoom.setOnClickListener {
            viewModel.checkAvailableToOpenAddRoom()
        }
        return binding.root
    }


    private fun initObservers() {
        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {

                it.getContentIfNotHandled {

                    this.binding.contentContainer.visible(true)
                    binding.login.gone(false)
                    binding.registration.gone(false)
                    val splitFio = it.name.split(" ")
                    var fio = ""
                    if (splitFio.size > 2) {
                        splitFio[1]?.let {
                            fio += it
                        }
                        splitFio[2]?.let {
                            fio += " " + it
                        }
                    } else {
                        splitFio[1]?.let {
                            fio += it
                        }
                    }

                    binding.welcomeText.text = getString(R.string.welcome_user, fio)
                }
            }
        }
        viewModel.getWithoutUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   this.binding.contentContainer.visible(true)
                }
            }
        }

        viewModel.getNavigateTo().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   when (it) {
                       "ADD_ROOM" ->  {
                           findNavController().navigate(R.id.action_WelcomeFragment_to_AddRoom)
                       }
                       "REGISTRATION" ->  {
                           findNavController().navigate(R.id.toRegistration)
                       }
                   }
                }

            }
        }
        viewModel.getEditPlacement().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    Log.d("WelcomeFragment", "open edit")
                    val bundle = bundleOf("placement" to it)
                    findNavController().navigate(R.id.EditPlacement, bundle)
                }
            }
        }

        viewModel.getPinForm().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    Log.d("WelcomeFragment", "open pin form")
                    val bundle = bundleOf("user" to it)
                    findNavController().navigate(R.id.toPinCode, bundle)
                }

            }
        }
        viewModel.getReadStoragePermission().observe(this) {
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
        viewModel.getPlacementList().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    requireActivity().findViewById<Toolbar>(R.id.toolbar).visible(false)

                    binding.userContainer.gone(false)
                    binding.premisesContainer.visible(false)
                    placementAdapter = PlacementAdapter(it, requireContext(), viewModel)
                    binding.premisesList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.premisesList.adapter = placementAdapter
                    Handler().postDelayed({
                        val position = (binding.premisesList.getLayoutManager() as LinearLayoutManager).findFirstVisibleItemPosition()
                        var viewholder = binding.premisesList.findViewHolderForAdapterPosition(position)

                        (viewholder!!.itemView as ConstraintLayout )?.let {
                            val root = it;
                            transmitAnchor = root.findViewById(R.id.transmitBalloonTriger)
                            paymentAnchor = root.findViewById(R.id.paymentBalloonTrigger)
                            showTransmitTooltip()
                        }
                    }, 200)
                }
            }
        }

        viewModel.getUpdatePosition().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                  if (placementAdapter != null)
                      placementAdapter.notifyItemChanged(it)
                }
            }
        }
        viewModel.getPersonalAccountPlacement().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val bundle = bundleOf("placement" to it)
                    findNavController().navigate(R.id.toPersonalAccountPlacement, bundle)
                }
            }
        }

        viewModel.getEditPlacementDialog().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val placement = it
                    val root = layoutInflater.inflate(R.layout.layout_edit_placement, null)
                    val editPlacementDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                    editPlacementDialog.setContentView(root)
                    root.findViewById<View>(R.id.editPlacement).setOnClickListener {
                        Log.d("WelcomeFragment", "edit placement")
                        editPlacementDialog.dismiss()
                        viewModel.selectEditPlacement(placement)
                    }
                    editPlacementDialog.show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.initCurrentUser()
        initTooltip()
    }

    private fun initTooltip() {
        balloonTransmit = Balloon.Builder(requireContext()).apply {
            setArrowSize(10)
            setArrowOrientation(ArrowOrientation.TOP)
            setCornerRadius(4f)
            setArrowColor(Color.WHITE)
            setBackgroundColor(Color.WHITE)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
            setMarginTop(4.dp)
            setLayout(R.layout.layout_tooltip_transmit)
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)
        }.build()

        balloonPayment = Balloon.Builder(requireContext()).apply {
            setArrowSize(10)
            setArrowOrientation(ArrowOrientation.TOP)
            setCornerRadius(4f)
            setArrowColor(Color.WHITE)
            setBackgroundColor(Color.WHITE)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
            setMarginTop(4.dp)
            setMarginRight(8.dp)
            setLayout(R.layout.layout_tooltip_payment)
            setBalloonAnimation(BalloonAnimation.FADE)
            setLifecycleOwner(lifecycleOwner)
        }.build()

        balloonPayment.getContentView().findViewById<View>(R.id.okTooltip).setOnClickListener{
            balloonPayment.dismiss()
            mainViewModel.needBackgroundShadow(false)
        }

        balloonTransmit.getContentView().findViewById<View>(R.id.okTooltip).setOnClickListener{
            balloonTransmit.dismiss()
            showPaymentTooltip()
        }

        balloonTransmit.setOnBalloonDismissListener {
            showPaymentTooltip()
        }

        balloonPayment.setOnBalloonDismissListener {
            mainViewModel.needBackgroundShadow(false)
        }
    }

    private fun showPaymentTooltip() {
        paymentAnchor.showAlignBottom(balloonPayment)
    }

    private fun showTransmitTooltip() {
        mainViewModel.needBackgroundShadow(true)
        transmitAnchor.showAlignBottom(balloonTransmit)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_READ_EXTERNAL -> {
                viewModel.setReadStoragePermission(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}