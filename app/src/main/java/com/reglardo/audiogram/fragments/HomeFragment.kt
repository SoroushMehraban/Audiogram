package com.reglardo.audiogram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.android.marsphotos.network.VoiceApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.R
import com.reglardo.audiogram.adapter.VoiceAdapter
import com.reglardo.audiogram.databinding.FragmentHomeBinding
import com.reglardo.audiogram.databinding.FragmentProfileBinding
import com.reglardo.audiogram.fragments.ViewModel.VoiceViewModel
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private val voiceViewModel: VoiceViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val response = VoiceApi.retrofitService.getHomeVoices(MainActivity.token)
            response.let {
                if (it.success) {
                    val voices = it.voices!!
                    val recyclerView = binding.homeVoiceRecyclerView
                    if (voices.isNotEmpty()) {
                        binding.noVoicePosted.visibility = View.GONE
                    }
                    else {
                        binding.noVoicePosted.visibility = View.VISIBLE
                    }
                    recyclerView.adapter = VoiceAdapter(voiceViewModel, voices)
                }
            }
        }
    }
}