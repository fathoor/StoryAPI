package com.fathoor.storyapi.view.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
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
        setupAnimation()
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
                        isRegistered.observe(this@RegisterActivity) { if (it) navigateToLogin() }
                        error.observe(this@RegisterActivity) { if (!it.isNullOrEmpty()) showToast(it) }
                    }
                }
            }
        }
    }

    private fun navigateToLogin() {
        Intent(this@RegisterActivity, LoginActivity::class.java).also {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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

    private fun setupAnimation() {
        ObjectAnimator.ofPropertyValuesHolder(
            binding.ivLogo,
            PropertyValuesHolder.ofFloat("scaleX", 1.1f),
            PropertyValuesHolder.ofFloat("scaleY", 1.1f)
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            duration = 5000
        }.start()

        val tvRegisterTitle = ObjectAnimator.ofFloat(binding.tvRegisterTitle, "alpha", 0f, 1f)
        val tvRegisterDescription = ObjectAnimator.ofFloat(binding.tvRegisterDescription, "alpha", 0f, 1f)
        val tvName = ObjectAnimator.ofFloat(binding.tvName, "alpha", 0f, 1f)
        val tiRegisterName = ObjectAnimator.ofFloat(binding.tiRegisterName, "alpha", 0f, 1f)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, "alpha", 0f, 1f)
        val tiRegisterEmail = ObjectAnimator.ofFloat(binding.tiRegisterEmail, "alpha", 0f, 1f)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, "alpha", 0f, 1f)
        val tiRegisterPassword = ObjectAnimator.ofFloat(binding.tiRegisterPassword, "alpha", 0f, 1f)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playSequentially(
                tvRegisterTitle,
                tvRegisterDescription,
                tvName,
                tiRegisterName,
                tvEmail,
                tiRegisterEmail,
                tvPassword,
                tiRegisterPassword,
                btnRegister
            )
            duration = 500
            start()
        }
    }
}