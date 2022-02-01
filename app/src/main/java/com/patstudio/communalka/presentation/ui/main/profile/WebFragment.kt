package com.patstudio.communalka.presentation.ui.main.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patstudio.communalka.R
import com.patstudio.communalka.common.utils.Event
import com.patstudio.communalka.data.model.PlacementMeter
import com.patstudio.communalka.databinding.*
import com.patstudio.communalka.presentation.ui.main.ProfileViewModel
import gone
import org.koin.android.viewmodel.ext.android.viewModel
import visible
import android.provider.Browser
import com.patstudio.communalka.presentation.ui.main.readings.ConsumptionHistoryViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.lang.Exception


class WebFragment : Fragment() {

    private var _binding: FragmentWebBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<ConsumptionHistoryViewModel>()
    private  val PREFERENCES_FILE_KEY = "settings_preferences"
    private  val SECURE_PREFS_FILE_KEY = "secure_preferences"

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWebBinding.inflate(inflater, container, false)
        initObserver()
        return binding.root
    }

    private fun initObserver() {
        viewModel.pdfBytes.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                  binding.pdfView.fromBytes(it)
                      .load()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()
        viewModel.downloadByPdf()
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MyDownLoadListener(private val context: Context): DownloadListener {



    override fun onDownloadStart(url: String?, p1: String?, p2: String?, p3: String?, p4: Long) {
        if (url != null) {


            var intent = Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }
}