package com.patstudio.communalka.presentation.ui.main.profile.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.patstudio.communalka.databinding.FragmentRequestBinding
import com.patstudio.communalka.presentation.ui.main.payment.PaymentsViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private val filterPayment by sharedViewModel<PaymentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRequestBinding.inflate(inflater, container, false)


        return binding.root
    }

    private fun initObservers() {

    }

    override fun onResume() {
        super.onResume()
        filterPayment.resetFilter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}