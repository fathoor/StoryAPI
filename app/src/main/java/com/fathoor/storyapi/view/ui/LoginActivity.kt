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
import com.fathoor.storyapi.databinding.ActivityLoginBinding
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var userToken: String

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
            btnLogin.setOnClickListener {
                if (!edLoginEmail.error.isNullOrEmpty() || !edLoginPassword.error.isNullOrEmpty()) {
                    when {
                        !edLoginEmail.error.isNullOrEmpty() -> showToast(edLoginEmail.error.toString())
                        !edLoginPassword.error.isNullOrEmpty() -> showToast(edLoginPassword.error.toString())
                    }
                } else {
                    loginViewModel.apply {
                        userLogin(
                            edLoginEmail.text.toString(),
                            edLoginPassword.text.toString()
                        ).also {
                            token.observe(this@LoginActivity) { userToken = it }
                            isLoading.observe(this@LoginActivity) { showLoading(it) }
                            isLoggedIn.observe(this@LoginActivity) { if (it) navigateWithToken(userToken) }
                            error.observe(this@LoginActivity) { if (!it.isNullOrEmpty()) showToast(it) }
                        }
                    }
                }
            }
        }
    }

    private fun navigateWithToken(token: String) {
        Intent(this@LoginActivity, MainActivity::class.java).also {
            it.putExtra(MainActivity.EXTRA_TOKEN, token)
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        binding.cpiLogin.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupAnimation() {
        ObjectAnimator.ofPropertyValuesHolder(
            binding.ivLoginLogo,
            PropertyValuesHolder.ofFloat("scaleX", 1.1f),
            PropertyValuesHolder.ofFloat("scaleY", 1.1f)
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            duration = 5000
        }.start()

        val tvLoginTitle = ObjectAnimator.ofFloat(binding.tvLoginTitle, "alpha", 0f, 1f)
        val tvLoginDescription = ObjectAnimator.ofFloat(binding.tvLoginDescription, "alpha", 0f, 1f)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, "alpha", 0f, 1f)
        val tiLoginEmail = ObjectAnimator.ofFloat(binding.tiLoginEmail, "alpha", 0f, 1f)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, "alpha", 0f, 1f)
        val tiLoginPassword = ObjectAnimator.ofFloat(binding.tiLoginPassword, "alpha", 0f, 1f)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playSequentially(
                tvLoginTitle,
                tvLoginDescription,
                tvEmail,
                tiLoginEmail,
                tvPassword,
                tiLoginPassword,
                btnLogin
            )
            duration = 500
            start()
        }
    }
}