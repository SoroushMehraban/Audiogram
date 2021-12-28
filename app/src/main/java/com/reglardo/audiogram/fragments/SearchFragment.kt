package com.reglardo.audiogram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.reglardo.audiogram.databinding.FragmentSearchBinding
import com.reglardo.audiogram.fragments.ViewModel.SearchViewModel


class SearchFragment : Fragment() {
    private val searchViewModel: SearchViewModel by viewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.searchBtn.setOnClickListener {
            val usernameIsEntered = binding.searchField.text.toString() != ""
            if (usernameIsEntered) {
                val username = binding.searchField.text.toString()
                searchViewModel.searchUsername(username)

                searchViewModel.searchResponse.observe(this, {
                    if (it.success) {
                        Log.i("SearchResponse", "success true")
                        val users = it.users
                        for (user in users!!) {
                            Log.i("SearchResponse", "username: ${user.username}")
                            Log.i("SearchResponse", "first name: ${user.firstName}")
                            Log.i("SearchResponse", "last name: ${user.lastName}")
                            Log.i("SearchResponse", "-----")
                        }
                    } else {
                        Log.i("SearchResponse", "success false")
                        Log.i("SearchResponse", it.message.toString())
                    }
                })
            }
        }
    }
}