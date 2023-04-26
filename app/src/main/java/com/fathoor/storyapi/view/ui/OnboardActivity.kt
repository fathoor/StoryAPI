package com.fathoor.storyapi.view.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fathoor.storyapi.databinding.ActivityOnboardBinding
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.OnboardViewModel

class OnboardActivity : AppCompatActivity() {
    private val binding by lazy { ActivityOnboardBinding.inflate(layoutInflater) }
    private val onboardViewModel by viewModels<OnboardViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var userToken: String
    private var hasLogin: Boolean = false
    private var hasLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupSplash()
        setupAction()
        setupAnimation()
    }

    private fun setupView() {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
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

    private fun setupAnimation() {
        ObjectAnimator.ofPropertyValuesHolder(
            binding.ivOnboardLogo,
            PropertyValuesHolder.ofFloat("scaleX", 1.1f),
            PropertyValuesHolder.ofFloat("scaleY", 1.1f)
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            duration = 5000
        }.start()

        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, "alpha", 0f, 1f)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, "alpha", 0f, 1f)

        AnimatorSet().apply {
            playSequentially(btnLogin, btnRegister)
            duration = 500
            startDelay = 500
        }.start()
    }
}
