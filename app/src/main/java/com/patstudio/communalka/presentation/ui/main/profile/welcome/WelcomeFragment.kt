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
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.MarginLayoutParamsCompat.setMarginEnd
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentWelcomeBinding
import com.patstudio.communalka.presentation.ui.main.WelcomeViewModel
import com.patstudio.communalka.presentation.ui.main.room.PlacementAdapter
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import gone
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import visible
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.balloon.*
import com.skydoves.balloon.extensions.dp


class WelcomeFragment : Fragment() {

    private val REQUEST_READ_EXTERNAL: Int = 111
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<WelcomeViewModel>()
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
                    val adapter = PlacementAdapter(it, requireContext(), requireActivity())
                    binding.premisesList.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.premisesList.adapter = adapter
                    Handler().postDelayed(Runnable {
                        val position = (binding.premisesList.getLayoutManager() as LinearLayoutManager).findFirstVisibleItemPosition()
                        var viewholder = binding.premisesList.findViewHolderForAdapterPosition(position)

                        (viewholder!!.itemView as ConstraintLayout )?.let {
                            val root = it;
                            transmitAnchor = root.findViewById(R.id.transmitBalloonTriger)
                            paymentAnchor = root.findViewById(R.id.paymentBalloonTrigger)
                            showTransmitTooltip()
                        }
                    }, 500)
//                    position?.let {
//                        val viewholder = binding.premisesList.findViewHolderForLayoutPosition(it)
//                        Log.d("WelcomeFragment", "viewholder "+viewholder)
//
//
//                        (viewholder!!.itemView as ConstraintLayout )?.let {
//                            val root = it;
//                            transmitAnchor = root.findViewById(R.id.transmitBalloonTriger)
//                            paymentAnchor = root.findViewById(R.id.paymentBalloonTrigger)
//                            showTransmitTooltip()
//                        }
//                    }


//                    binding.premisesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                        override fun onScrollStateChanged(
//                            recyclerView: RecyclerView,
//                            newState: Int
//                        ) {
//                            super.onScrollStateChanged(recyclerView, newState)
//
//
//
//                        }
//                    })
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        val needLogin = arguments?.getBoolean("need_login", true)
        Log.d("WelcomeFragment", needLogin.toString())
        if (needLogin != null) {
            viewModel.setNeedEnterPin(needLogin)
            viewModel.initCurrentUser()
        } else {
            viewModel.initCurrentUser()
        }
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