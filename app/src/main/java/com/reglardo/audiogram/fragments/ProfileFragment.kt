package com.reglardo.audiogram.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.ContextWrapper
import android.content.Intent
import android.database.Cursor
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.load
import com.example.android.marsphotos.network.URL
import com.example.android.marsphotos.network.UserApi
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.R
import com.reglardo.audiogram.authentication.LoginActivity
import com.reglardo.audiogram.databinding.FragmentProfileBinding
import com.reglardo.audiogram.fragments.ViewModel.ProfileViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import java.io.File
import okhttp3.RequestBody


private const val ARG_PARAM1 = "param1"

class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var username: String? = null

    companion object {
        @JvmStatic
        fun newInstance(username: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, username)
                }
            }
    }

    private val imagePickLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null && data.data != null) {
                    val imageFile = getBitmapFile(data)
                    val fileBody = MultipartBody.Part.createFormData(
                        "file", imageFile.name,
                        RequestBody.create(MediaType.parse("image/*"), imageFile)
                    )

                    profileViewModel.uploadProfilePhoto(fileBody)
                    profileViewModel.profileUpdateResponse.observe(this, {
                        if (it.success) {
                            profileViewModel.getMyProfile()
                        } else {
                            Log.i("Error", it.message)
                        }
                    })
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        if (username != null) {
            profileViewModel.getProfile(username!!)
        } else {
            profileViewModel.getMyProfile()
        }
        profileViewModel.profileResponse.observe(viewLifecycleOwner, {
            if (it.success) {
                binding.username.text = it.username
                binding.name.text = "(${it.firstName} ${it.lastName})"
                binding.followersNumber.text = it.followers.toString()
                binding.followingNumber.text = it.followings.toString()
                binding.voicesNumber.text = it.voices.toString()
                val imgUri = "${URL.BASE_URL}${it.image}".toUri().buildUpon().scheme("http").build()
                binding.profileImage.load(imgUri)

                if (it.isMe == true) {
                    binding.profileBtn.text = getString(R.string.change_profile)
                    binding.profileBtn.setOnClickListener {
                        openGalleryForImage()
                    }
                    binding.logoutBtn.visibility = View.VISIBLE
                }
                else {
                    binding.profileBtn.text = getString(R.string.follow)
                }
            }
        })

        binding.logoutBtn.setOnClickListener {
            val contextWrapper = ContextWrapper(requireActivity().applicationContext)
            val documentDirectory =
                contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val tokenFile = File(documentDirectory, "token.gram")
            tokenFile.delete()
            openLoginActivity()
        }

        return binding.root
    }


    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickLauncher.launch(intent)
    }

    private fun openLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        // We set the following flags to disable coming back from MainActivity to here
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
    }

    private fun getBitmapFile(data: Intent): File {
        val selectedImage: Uri? = data.data
        val cursor: Cursor = requireActivity().contentResolver.query(
            selectedImage!!,
            arrayOf(MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )!!
        cursor.moveToFirst()
        val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val selectedImagePath: String = cursor.getString(idx)
        cursor.close()
        return File(selectedImagePath)
    }

}