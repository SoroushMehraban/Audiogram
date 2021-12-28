package com.reglardo.audiogram

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reglardo.audiogram.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.reglardo.audiogram.fragments.*


class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val recorderFragment = RecorderFragment()
    private val notificationFragment = NotificationFragment()
    private val profileFragment = ProfileFragment()

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(recorderFragment)
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

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}