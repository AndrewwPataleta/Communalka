package com.communalka.app.presentation.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.communalka.app.databinding.FragmentConnectDeveloperBinding

class ConnectDeveloperFragment : Fragment() {

    private var _binding: FragmentConnectDeveloperBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConnectDeveloperBinding.inflate(inflater, container, false)

        return binding.root
    }


    private fun initObservers() {

    }
    private fun initListeners() {
        binding.sendToDeveloper.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {

        var mIntent = Intent(Intent.ACTION_SEND)

        mIntent.let {
            mIntent.data = Uri.parse("mailto:")
            mIntent.type = "text/plain"

            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@test.communalka.site","stqagm@gmail.com"))

            it.putExtra(Intent.EXTRA_SUBJECT, binding.senderEdit.text.toString())

            it.putExtra(Intent.EXTRA_TEXT, binding.fioEdit.text.toString())
        }

        try {

            startActivity(Intent.createChooser(mIntent, "Выберите приложение"))
        }
        catch (e: Exception){

            e.printStackTrace()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}