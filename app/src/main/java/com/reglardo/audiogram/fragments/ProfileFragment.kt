package com.reglardo.audiogram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.authentication.AuthenticationViewModel
import com.reglardo.audiogram.databinding.FragmentProfileBinding
import com.reglardo.audiogram.fragments.ViewModel.ProfileViewModel


class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.getMyProfile()
        profileViewModel.profileResponse.observe(this, {
            if (it.success) {
                binding.username.text = it.username
                binding.name.text = "(${it.firstName} ${it.lastName})"
                binding.followersNumber.text = it.followers.toString()
                binding.followingNumber.text = it.followings.toString()
                binding.voicesNumber.text = it.voices.toString()
            }
        })
    }
}