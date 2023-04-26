package com.fathoor.storyapi.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.fathoor.storyapi.databinding.ActivityDetailStoryBinding
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.DetailStoryViewModel

class DetailStoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailStoryBinding.inflate(layoutInflater) }
    private val detailStoryViewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var userToken: String? = null
    private var storyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAppBar()
        setupViewModel()
    }

    private fun setupView() {
        userToken = intent.getStringExtra(EXTRA_TOKEN) ?: ""
        storyId = intent.getStringExtra(EXTRA_STORY) ?: ""
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            @Suppress("DEPRECATION")
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setupViewModel() {
        detailStoryViewModel.apply {
            userToken?.let { token ->
                storyId?.let { id ->
                    userStoryDetail(token, id).also {
                        storyDetail.observe(this@DetailStoryActivity) {
                            if (it != null) {
                                showDetail(it)
                            } else {
                                @Suppress("DEPRECATION")
                                onBackPressed()
                            }
                        }
                        isLoading.observe(this@DetailStoryActivity) { showLoading(it) }
                        error.observe(this@DetailStoryActivity) { if (!it.isNullOrEmpty()) showToast(it) }
                    }
                }
            }
        }
    }

    private fun showDetail(story: Story) {
        binding.apply {
            tvDetailName.text = story.name
            Glide.with(this@DetailStoryActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
            tvDetailDescription.text = story.description
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@DetailStoryActivity, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showLoading(state: Boolean) {
        binding.cpiDetail.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        const val EXTRA_STORY = "extra_story"
    }
}