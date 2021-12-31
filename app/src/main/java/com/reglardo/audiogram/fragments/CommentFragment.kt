package com.reglardo.audiogram.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import com.example.android.marsphotos.network.VoiceApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.adapter.CommentAdapter
import com.reglardo.audiogram.databinding.FragmentCommentBinding
import kotlinx.coroutines.launch
import java.lang.Exception

private const val ARG_PARAM1 = "param1"

class CommentFragment : Fragment() {

    private var voiceId: String? = null

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance(voiceId: String) =
            CommentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, voiceId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            voiceId = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)

        updateComments()

        binding.sendCommentBtn.setOnClickListener {
            lifecycleScope.launch {
                hideKeyboard(it)
                val comment = binding.commentField.text.toString()
                binding.commentField.setText("")
                if (comment != "") {
                    try {
                        val response = VoiceApi.retrofitService.comment(MainActivity.token, voiceId!!, comment)
                        if (response.success) {
                            updateComments()
                        }
                    } catch (e: Exception) {
                        binding.commentField.setText(comment)
                    }
                }
            }
        }
        return binding.root
    }

    private fun updateComments() {
        lifecycleScope.launch {
            try {
                val response = VoiceApi.retrofitService.getComments(MainActivity.token, voiceId!!)
                if (response.success)
                    binding.commentRecyclerView.adapter = CommentAdapter(response.comments!!)
            } catch (e: Exception) {}
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}