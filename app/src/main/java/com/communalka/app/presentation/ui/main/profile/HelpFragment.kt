package com.communalka.app.presentation.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.communalka.app.R
import com.communalka.app.databinding.FragmentHelpUserBinding
import org.koin.android.viewmodel.ext.android.viewModel

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<HelpViewModel>()
    private lateinit var adapter: YoutubeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpUserBinding.inflate(inflater, container, false)
        viewModel.initVideo()

        return binding.root
    }

    private fun initObservers() {
        viewModel.video.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    adapter = YoutubeAdapter(it, viewModel)
                    binding.videoContainer.layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    binding.videoContainer.adapter = adapter
                }
            }
        }

        viewModel.item.observe(viewLifecycleOwner) {
            if (!it.hasBeenHandled.get()) {
                it.getContentIfNotHandled {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+it.snippet.resourceId.videoId))
                    startActivity(browserIntent)
                }
            }
        }
    }

    private fun initListeners() {
        binding.text.setOnClickListener {
            findNavController().navigate(R.id.toFaq)
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