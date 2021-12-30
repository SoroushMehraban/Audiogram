package com.reglardo.audiogram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.reglardo.audiogram.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.reglardo.audiogram.authentication.LoginActivity
import com.reglardo.audiogram.fragments.*


class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val recorderFragment = RecorderFragment()
    private val notificationFragment = NotificationFragment()
    private val profileFragment = ProfileFragment()

    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var token: String
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentFrom = intent?.extras?.getString(RecordingListActivity.FROM).toString()
        if (intentFrom == "UploadPhoto") {
            replaceFragment(profileFragment)
            binding.navigationBar.selectedItemId = R.id.icon_profile
        }
        else {
            replaceFragment(recorderFragment)
            binding.navigationBar.selectedItemId = R.id.icon_recorder
        }

        binding.navigationBar.selectedItemId = R.id.icon_recorder
        binding.navigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.icon_home -> replaceFragment(homeFragment)
                R.id.icon_search -> replaceFragment(searchFragment)
                R.id.icon_recorder -> replaceFragment(recorderFragment)
                R.id.icon_notification -> replaceFragment(notificationFragment)
                R.id.icon_profile -> replaceFragment(profileFragment)
            }
            true
        }

    }

    override fun onBackPressed() {
        val profileSearchFragment = supportFragmentManager.findFragmentByTag("ProfileSearch")
        val commentFromHomeFragment = supportFragmentManager.findFragmentByTag("fromHomeFragment")
        val commentFromProfileFragment = supportFragmentManager.findFragmentByTag("fromProfileFragment")
        val commentFromSearchFragment = supportFragmentManager.findFragmentByTag("fromSearchFragment")

        if (profileSearchFragment != null && profileSearchFragment.isVisible) {
            replaceFragment(searchFragment)
        }
        else if (commentFromHomeFragment != null && commentFromHomeFragment.isVisible){
            replaceFragment(homeFragment)
        }
        else if (commentFromProfileFragment != null && commentFromProfileFragment.isVisible){
            replaceFragment(profileFragment)
        }
        else if (commentFromSearchFragment != null && commentFromSearchFragment.isVisible){
            replaceFragment(searchFragment)
        }
        else {
            super.onBackPressed()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}