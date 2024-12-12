package com.dicoding.tanicare.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.tanicare.databinding.ActivityLoginBinding
import com.dicoding.tanicare.ui.main.MenuActivity
import com.dicoding.tanicare.utils.TokenManager
import com.dicoding.tanicare.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addTextWatchers()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (isValidInput(email, password)) {
                performLogin(email, password)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { result ->
            binding.lottieLoading.visibility = View.GONE
            result.onSuccess { loginResponse ->
                TokenManager.saveAccessToken(this, loginResponse.access_token)
                TokenManager.saveLoginStatus(this, true)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }.onFailure { exception ->
                Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTextWatchers() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            false
        } else {
            binding.etEmail.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        return if (password.length < 8) {
            binding.etPassword.error = "Password must have at least 8 characters"
            false
        } else {
            binding.etPassword.error = null
            true
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        return isEmailValid && isPasswordValid
    }

    private fun performLogin(email: String, password: String) {
        binding.lottieLoading.visibility = View.VISIBLE
        viewModel.login(email, password)
    }
}
