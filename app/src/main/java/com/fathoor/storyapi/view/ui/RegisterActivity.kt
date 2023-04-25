package com.fathoor.storyapi.view.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.fathoor.storyapi.databinding.ActivityRegisterBinding
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.apply {
            btnRegister.setOnClickListener {
                registerViewModel.apply {
                    userRegister(
                        edRegisterName.text.toString(),
                        edRegisterEmail.text.toString(),
                        edRegisterPassword.text.toString()
                    ).also {
                        isLoading.observe(this@RegisterActivity) { showLoading(it) }
                        isRegistered.observe(this@RegisterActivity) { if (!it) navigateToLogin() }
                        error.observe(this@RegisterActivity) { if (!it.isNullOrEmpty()) showToast(it) }
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        Intent(this@RegisterActivity, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        binding.cpiRegister.visibility = if (state) View.VISIBLE else View.GONE
    }
}