package com.reglardo.audiogram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.android.marsphotos.network.UserApi
import com.example.android.marsphotos.network.VoiceApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.adapter.VoiceAdapter
import com.reglardo.audiogram.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.lang.Exception


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var updateThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        Thread {
            while (true) {
                updateHomeVoices(this)
                Thread.sleep(2000)
            }
        }.start()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val fragment = this
        updateHomeVoices(fragment)
    }


    private fun updateHomeVoices(fragment: HomeFragment) {
        lifecycleScope.launch {
            try {
                val response = VoiceApi.retrofitService.getHomeVoices(MainActivity.token)
                response.let {
                    if (it.success) {
                        val voices = it.voices!!
                        val recyclerView = binding.homeVoiceRecyclerView
                        if (voices.isNotEmpty()) {
                            binding.noVoicePosted.visibility = View.GONE
                        } else {
                            binding.noVoicePosted.visibility = View.VISIBLE
                        }
                        recyclerView.adapter = VoiceAdapter(fragment, voices, "fromHomeFragment")
                    }
                }
            } catch (e: Exception) {}
        }
    }
}