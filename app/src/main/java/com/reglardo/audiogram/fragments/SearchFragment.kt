package com.reglardo.audiogram.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.reglardo.audiogram.adapter.RecordingAdapter
import com.reglardo.audiogram.adapter.SearchAdapter
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
            hideKeyboard(it)

            val usernameIsEntered = binding.searchField.text.toString() != ""
            if (usernameIsEntered) {
                val username = binding.searchField.text.toString()
                searchViewModel.searchUsername(username)

                searchViewModel.searchResponse.observe(this, {
                    if (it.success) {
                        val users = it.users!!
                        val recyclerView = binding.searchRecyclerView
                        if (users.isNotEmpty()) {
                            binding.noUserFound.visibility = View.INVISIBLE
                        }
                        else {
                            binding.noUserFound.visibility = View.VISIBLE
                        }
                        recyclerView.adapter = SearchAdapter(this, users)

                    } else {
                        Log.i("Error", "success false")
                        Log.i("Error", it.message.toString())
                    }
                })
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}