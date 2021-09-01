package com.patstudio.communalka.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.bumptech.glide.Glide
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.ActivityMainBinding
import com.patstudio.communalka.presentation.ui.main.room.EditRoomViewModel
import com.patstudio.communalka.presentation.ui.splash.MainViewModel
import com.patstudio.communalka.presentation.ui.splash.SplashViewModel
import gone
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.content_main.*

import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import visible


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainViewModel>()

    private val editRoomView by viewModel<EditRoomViewModel>()

    private fun initObservers() {
        viewModel.getNeedShadow().observe(this) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    if (it) {
                        binding.backgroundShadow.visible(false)
                    } else {
                        binding.backgroundShadow.gone(false)
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.deleteRoom.setOnClickListener {
            editRoomView.selectDelete()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initListeners()
        val navController = findNavController(R.id.nav_host_fragment_content)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(Navigation.findNavController(this, R.id.nav_host_fragment_content))
        bottomNavigationView.setOnNavigationItemSelectedListener {item ->
            onNavDestinationSelected(item, Navigation.findNavController(this, R.id.nav_host_fragment_content))
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.WelcomeFragment -> {
                    toolbar.visibility = View.GONE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.LoginFragment-> {
                    toolbar.visibility = View.GONE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.Registration-> {
                    toolbar.visibility = View.GONE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.Profile-> {
                    toolbar.visibility = View.VISIBLE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.AddRoom-> {
                    toolbar.visibility = View.VISIBLE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.ConfirmSms-> {
                    toolbar.visibility = View.GONE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.Request-> {
                    toolbar.visibility = View.VISIBLE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.PinCode-> {
                    toolbar.visibility = View.GONE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.EditPlacement-> {
                    toolbar.visibility = View.VISIBLE
                    deleteRoom.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.EntranceSecurity-> {
                    toolbar.visibility = View.VISIBLE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.EditEmail-> {
                    toolbar.visibility = View.VISIBLE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.EditPinCode-> {
                    toolbar.visibility = View.VISIBLE
                    deleteRoom.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }
        initObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}