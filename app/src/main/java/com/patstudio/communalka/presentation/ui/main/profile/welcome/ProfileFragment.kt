package com.patstudio.communalka.presentation.ui.main.profile.welcome

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.patstudio.communalka.BuildConfig
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.FragmentProfileBinding
import com.patstudio.communalka.presentation.ui.MainActivity
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import com.patstudio.communalka.presentation.ui.main.profile.SwitchProfileAdapter
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import com.skydoves.balloon.extensions.dp
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import ru.tinkoff.acquiring.sdk.TinkoffAcquiring
import ru.tinkoff.acquiring.sdk.models.enums.CheckType
import ru.tinkoff.acquiring.sdk.models.options.CustomerOptions
import ru.tinkoff.acquiring.sdk.models.options.screen.PaymentOptions
import ru.tinkoff.acquiring.sdk.models.options.screen.SavedCardsOptions
import visible

class   ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<ProfileViewModel>()
    private val mainViewModel by viewModel<MainViewModel>()
    private lateinit var adapter: SwitchProfileAdapter
    private lateinit var switchUserBinding: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun setAuthNavigation() {
        this.binding.profileText.setOnClickListener {
            findNavController().navigate(R.id.PersonalInfo)
        }
        this.binding.securityText.setOnClickListener {
            findNavController().navigate(R.id.toEntrance)
        }
        this.binding.notificationText.setOnClickListener {
            findNavController().navigate(R.id.toUserNotificationSettings)
        }
        this.binding.helpText.setOnClickListener {
            findNavController().navigate(R.id.toHelp)
        }
        this.binding.cardsText.setOnClickListener {
            val tinkoffAcquiring = TinkoffAcquiring(BuildConfig.TERMINAL_KEY, BuildConfig.PUBLIC_KEY)

            val paymentOptions =
                SavedCardsOptions().setOptions {
                    customerOptions {
                        checkType = CheckType.NO.toString()
                        customerKey = "1"
                    }
                }
            tinkoffAcquiring.openSavedCardsScreen(requireActivity(), paymentOptions, 1234)
        }
    }

    private fun removeAuthNavigation() {
        this.binding.profileText.setOnClickListener {}
        this.binding.securityText.setOnClickListener {}
    }

    private fun haveNoAuthUser() {
        removeAuthNavigation()
        this.binding.avatar.gone(false)
        this.binding.userFio.gone(false)
        this.binding.iconDown.gone(false)
        this.binding.logoutGroup.gone(false)
        this.binding.loginBtn.visible(false)
        this.binding.profileText.setTextColor(resources.getColor(R.color.gray_dark))
        this.binding.cardsText.setTextColor(resources.getColor(R.color.gray_dark))
        this.binding.securityText.setTextColor(resources.getColor(R.color.gray_dark))
        this.binding.notificationText.setTextColor(resources.getColor(R.color.gray_dark))

        this.binding.profileArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)
        this.binding.cardsArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)
        this.binding.securityArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)
        this.binding.notificationArrow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray_dark), android.graphics.PorterDuff.Mode.MULTIPLY)


    }

    private fun initObservers() {
        viewModel.getUser().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    setAuthNavigation()
                    this.binding.userFio.text = it.name
                    Log.d("ProfileFragment", "path "+it.photoPath)
                    if (it.photoPath.length > 0) {
                        binding.avatar.setPadding(0)
                        binding.avatar.setImageURI(Uri.parse(it.photoPath))
                    } else {
                        binding.avatar.setPadding(16.dp)
                        binding.avatar.setImageResource(requireContext().resources.getIdentifier("ic_profile", "drawable", requireActivity().packageName))
                        binding.avatar.setBackgroundResource(requireContext().resources.getIdentifier("circle_gray_background", "drawable", requireActivity().packageName))
                    }

                }
            }
        }

        viewModel.getHaveNoAuth().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    haveNoAuthUser()
                }
            }
        }

        viewModel.getCloseSwitchDialog().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                   if (it) {
                       switchUserBinding.dismiss()
                   }
                }
            }
        }

        viewModel.getShowSwitchUsers().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    adapter = SwitchProfileAdapter(it, viewModel)
                    val root = layoutInflater.inflate(R.layout.layout_switch_users, null)
                    switchUserBinding = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
                    switchUserBinding.setContentView(root)
                    root.findViewById<RecyclerView>(R.id.profileList).layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    root.findViewById<RecyclerView>(R.id.profileList).adapter = adapter
                    root.findViewById<View>(R.id.login).setOnClickListener {
                        switchUserBinding.dismiss()
                        (requireActivity() as MainActivity).clearBackground()
                        val bundle = bundleOf("type" to "default")
                        findNavController().navigate(R.id.toLogin, bundle)
                    }
                    switchUserBinding.show()
                }
            }
        }
    }

    private fun initListeners() {
        this.binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.WelcomeFragment)
        }
        this.binding.profileText.setOnClickListener {
            findNavController().navigate(R.id.PersonalInfo)
        }
        this.binding.InfoAppText.setOnClickListener {
            findNavController().navigate(R.id.AboutApp)
        }
        this.binding.logoutText.setOnClickListener {
            viewModel.logout()
        }
        this.binding.userFio.setOnClickListener {
            viewModel.selectChangeProfile()
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