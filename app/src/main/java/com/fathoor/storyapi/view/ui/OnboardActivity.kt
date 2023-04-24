package com.fathoor.storyapi.view.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fathoor.storyapi.databinding.ActivityOnboardBinding
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.OnboardViewModel

class OnboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardBinding
    private val onboardViewModel by viewModels<OnboardViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var userToken: String
    private var hasLogin: Boolean = false
    private var hasLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupSplash()
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

    private fun setupViewModel() {
        onboardViewModel.apply {
            token.observe(this@OnboardActivity) { userToken = it }
            hasToken.observe(this@OnboardActivity) { hasLogin = it }
            isLoading.observe(this@OnboardActivity) { hasLoaded = !it }
        }
    }

    private fun setupSplash() {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                if (hasLogin && hasLoaded) {
                    navigateWithToken(userToken)
                    false
                } else !(!hasLogin && hasLoaded)
            }
            setOnExitAnimationListener { it.remove() }
        }
    }

    private fun navigateWithToken(token: String) {
        Intent(this@OnboardActivity, MainActivity::class.java).also {
            it.putExtra(MainActivity.EXTRA_TOKEN, token)
            startActivity(it)
            finish()
        }
    }

    private fun setupAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                Intent(this@OnboardActivity, LoginActivity::class.java).also { startActivity(it) }
            }

            btnRegister.setOnClickListener {
                Intent(this@OnboardActivity, RegisterActivity::class.java).also { startActivity(it) }
            }
        }
    }
}
