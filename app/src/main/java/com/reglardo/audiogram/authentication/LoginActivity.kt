package com.reglardo.audiogram.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.reglardo.audiogram.MainActivity
import com.reglardo.audiogram.R
import com.reglardo.audiogram.databinding.ActivityLoginBinding
import com.reglardo.audiogram.network.LoginData

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    companion object {
        const val SIGN_UP = "sign up"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        openMainActivity() // temporary

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newUser.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val signUpSuccessful = intent?.extras?.getBoolean(SIGN_UP)
        if (signUpSuccessful == true) {
            binding.signUpSuccessful.text = getString(R.string.registered_sucessfully)
        }

        binding.loginBtn.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            val dataIsMissing = username == "" || password == ""
            if (dataIsMissing) {
                binding.errorMsg.text = getString(R.string.data_is_missing_error)
            } else {
                val loginData = LoginData(username, password)
                viewModel.login(loginData)
                viewModel.loginAuthenticationResponse.observe(this, {
                    if (it.success) {
                        MainActivity.token = it.message
                        openMainActivity()
                    } else {
                        binding.errorMsg.text = "Error: ${it.message}"
                    }
                })
            }
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        // We set the following flags to disable coming back from MainActivity to here
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
    }
}