package com.fathoor.storyapi.view.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.fathoor.storyapi.databinding.ActivityLoginBinding
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupAction()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
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
        binding?.apply {
            btnLogin.setOnClickListener {
                if (!edLoginEmail.error.isNullOrEmpty() || !edLoginPassword.error.isNullOrEmpty()) {
                    when {
                        !edLoginEmail.error.isNullOrEmpty() -> showSnackbar(edLoginEmail.error.toString())
                        !edLoginPassword.error.isNullOrEmpty() -> showSnackbar(edLoginPassword.error.toString())
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
                            error.observe(this@LoginActivity) { if (!it.isNullOrEmpty()) showSnackbar(it) }
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

    private fun showSnackbar(message: String) {
        Snackbar.make(binding?.root as View, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        binding?.cpiLogin?.visibility = if (state) View.VISIBLE else View.GONE
    }
}