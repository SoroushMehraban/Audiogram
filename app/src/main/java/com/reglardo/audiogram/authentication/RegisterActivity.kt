package com.reglardo.audiogram.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.R
import com.reglardo.audiogram.databinding.ActivityRegisterBinding
import com.reglardo.audiogram.network.SignUpData

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerBtn.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.emailAddress.text.toString()
            val username = binding.emailAddress.text.toString()
            val password = binding.password.text.toString()
            val passwordConfirmation = binding.passwordConfirmation.text.toString()

            val dataIsMissing = firstName == "" || lastName == "" || email == "" || username == ""
                    || password == "" || passwordConfirmation == ""

            if (dataIsMissing) {
                binding.errorMsg.text = getString(R.string.fill_data_error)
            } else {
                val signUpData = SignUpData(
                    firstName, lastName, email, username, password,
                    passwordConfirmation
                )
                viewModel.signUp(signUpData)
                viewModel.signUpResponse.observe(this, {
                    if (it.success) {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra(LoginActivity.SIGN_UP, true)
                        startActivity(intent)
                    } else {
                        binding.errorMsg.text = "Error: ${it.message}"
                    }
                })
            }

        }
    }
}