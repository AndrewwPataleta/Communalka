package com.patstudio.communalka.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.patstudio.communalka.R
import com.patstudio.communalka.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.WelcomeFragment -> {
                    toolbar.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.LoginFragment-> {
                    toolbar.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.Registration-> {
                    toolbar.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.ConfirmSms-> {
                    toolbar.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                R.id.Request-> {
                    toolbar.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.PinCode-> {
                    toolbar.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content)

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}