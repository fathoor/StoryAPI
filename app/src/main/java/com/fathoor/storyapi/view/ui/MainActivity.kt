package com.fathoor.storyapi.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fathoor.storyapi.R
import com.fathoor.storyapi.databinding.ActivityMainBinding
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.view.adapter.StoryAdapter
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var userToken: String? = null
    private var storyList: List<Story>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAppBar()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
    }

    private fun setupView() {
        userToken = intent.getStringExtra(EXTRA_TOKEN) ?: ""
        setupSwipeRefresh()
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            setNavigationOnClickListener { showAlertDialog() }
            setOnMenuItemClickListener { navigateToMenu(it) }
        }
    }

    private fun setupViewModel() {
        mainViewModel.apply {
            userToken?.let { token ->
                userStoryList(token).also {
                    isLoading.observe(this@MainActivity) { showLoading(it) }
                    error.observe(this@MainActivity) { if (!it.isNullOrEmpty()) showToast(it) }
                    story.observe(this@MainActivity) {
                        storyList = it
                        setupRecyclerView(it)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(story: List<Story>) {
        val storyAdapter = StoryAdapter()
        storyAdapter.submitList(story)

        binding.rvStory.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        storyAdapter.setOnClickCallback(object : StoryAdapter.OnClickCallback {
            override fun onClicked(data: Story) {
                Intent(this@MainActivity, DetailStoryActivity::class.java).also {
                    it.putExtra(DetailStoryActivity.EXTRA_TOKEN, userToken)
                    it.putExtra(DetailStoryActivity.EXTRA_STORY, data.id)
                    startActivity(it)
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        val swipeRefresh: SwipeRefreshLayout = binding.srlStory
        swipeRefresh.apply {
            setOnRefreshListener {
                setupViewModel()
                isRefreshing = false
            }
        }
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(this@MainActivity, R.style.AlertDialog_App)
            .setTitle(getString(R.string.logout_button))
            .setMessage(getString(R.string.dialog_logout))
            .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                userLogout()
                Intent(this@MainActivity, OnboardActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                    finish()
                }
            }
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun navigateToMenu(menu: MenuItem): Boolean {
        return when (menu.itemId) {
            R.id.action_add -> {
                Intent(this@MainActivity, AddStoryActivity::class.java).also {
                    it.putExtra(AddStoryActivity.EXTRA_TOKEN, userToken)
                    startActivity(it)
                }
                true
            }
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> false
        }
    }

    private fun userLogout() {
        mainViewModel.userLogout()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        binding.cpiMain.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}