package com.dicoding.tanicare.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.tanicare.R
import com.dicoding.tanicare.databinding.ActivityMainBinding
import com.dicoding.tanicare.ui.login.LoginActivity
import com.dicoding.tanicare.ui.register.RegisterActivity
import com.dicoding.tanicare.utils.TokenManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (TokenManager.isLoggedIn(this)) {
            startActivity(Intent(this, MenuActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            startButtonAnimation(binding.btnLogin)
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnRegister.setOnClickListener {
            startButtonAnimation(binding.btnRegister)
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun startButtonAnimation(view: View) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.scale)
        view.startAnimation(anim)
    }
}
