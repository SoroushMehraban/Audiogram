package com.reglardo.audiogram.authentication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
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
            hideKeyboard(it)
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.emailAddress.text.toString()
            val username = binding.username.text.toString()
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

                viewModel.signUpAuthenticationResponse.observe(this, {
                    if (it.success) {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra(LoginActivity.SIGN_UP, true)
                        startActivity(intent)
                    } else {
                        binding.errorMsg.text = "Error: ${it.message}"
                    }
                })

                viewModel.connectionFailed.observe(this, { connectionFailed ->
                    if (connectionFailed == true) {
                        binding.errorMsg.text = getString(R.string.connection_error)
                    }
                })
            }

        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}