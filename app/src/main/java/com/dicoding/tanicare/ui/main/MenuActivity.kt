package com.dicoding.tanicare.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dicoding.tanicare.R
import com.dicoding.tanicare.data.local.AppDatabase
import com.dicoding.tanicare.databinding.ActivityMenuBinding
import com.dicoding.tanicare.ui.history.HistoryFragment
import com.dicoding.tanicare.ui.upload.UploadFragment
import com.dicoding.tanicare.utils.TokenManager
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrentFragment(UploadFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_upload -> {
                    setCurrentFragment(UploadFragment())
                    true
                }

                R.id.nav_history -> {
                    setCurrentFragment(HistoryFragment())
                    true
                }

                else -> false
            }
        }

        binding.ivLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.fragment_container, fragment).commit()
    }


    private fun performLogout() {
        lifecycleScope.launch {
            val historyDao = AppDatabase.getInstance(this@MenuActivity).historyDao()
            historyDao.deleteAllHistory()
        }

        TokenManager.saveAccessToken(this, "")
        TokenManager.saveLoginStatus(this, false)

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
