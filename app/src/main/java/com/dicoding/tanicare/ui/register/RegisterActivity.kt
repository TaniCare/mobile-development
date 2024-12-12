package com.dicoding.tanicare.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.tanicare.databinding.ActivityRegisterBinding
import com.dicoding.tanicare.ui.main.MenuActivity
import com.dicoding.tanicare.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addTextWatchers()

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (isValidInput(name, email, password, confirmPassword)) {
                viewModel.register(name, email, password)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.lottieLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }

        viewModel.registerResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                navigateToMenu()
            }.onFailure { exception ->
                Toast.makeText(this, "Registration failed ${exception.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun addTextWatchers() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateName()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateConfirmPassword()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateName(): Boolean {
        val name = binding.etName.text.toString().trim()
        return if (name.length < 6) {
            binding.etName.error = "Name must have at least 6 characters"
            false
        } else {
            binding.etName.error = null
            true
        }
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

    private fun validateConfirmPassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        return if (confirmPassword != password) {
            binding.etConfirmPassword.error = "Password confirmation does not match"
            false
        } else {
            binding.etConfirmPassword.error = null
            true
        }
    }

    private fun isValidInput(
        name: String, email: String, password: String, confirmPassword: String
    ): Boolean {
        val isNameValid = validateName()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()

        if (!isNameValid) {
            binding.etName.error = "Name cannot be empty"
            binding.etName.requestFocus()
        }

        return isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    private fun navigateToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
