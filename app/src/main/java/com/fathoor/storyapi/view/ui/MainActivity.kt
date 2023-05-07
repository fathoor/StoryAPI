package com.fathoor.storyapi.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fathoor.storyapi.R
import com.fathoor.storyapi.databinding.ActivityMainBinding
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.view.adapter.LoadingStateAdapter
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
    private var storyList: PagingData<Story>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        setupViewModel()
    }

    private fun setupView() {
        userToken = intent.getStringExtra(EXTRA_TOKEN) ?: ""
        setupSwipeRefresh()
        setupAppBar()
        setupFAB()
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            setNavigationOnClickListener { startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS)) }
            setOnMenuItemClickListener { navigateToMenu(it) }
        }
    }

    private fun setupFAB() {
        binding.apply {
            fabAdd.setOnClickListener {
                Intent(this@MainActivity, AddStoryActivity::class.java).also {
                    it.putExtra(AddStoryActivity.EXTRA_TOKEN, userToken)
                    startActivity(it)
                }
            }

            fabTop.setOnClickListener {
                rvStory.smoothScrollToPosition(0)
            }

            rvStory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        fabAdd.hide()
                        fabTop.show()
                    } else {
                        fabAdd.show()
                        fabTop.hide()
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!rvStory.canScrollVertically(-1)) { fabAdd.show() }
                }
            })
        }
    }

    private fun setupViewModel() {
        mainViewModel.apply {
            userStoryList().observe(this@MainActivity) {
                storyList = it
                setupRecyclerView(it)
            }
        }
    }

    private fun setupRecyclerView(story: PagingData<Story>) {
        val storyAdapter = StoryAdapter()
        storyAdapter.submitData(lifecycle, story)

        binding.rvStory.apply {
            adapter = storyAdapter.withLoadStateFooter(LoadingStateAdapter { storyAdapter.retry() })
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
            R.id.action_map -> {
                Intent(this@MainActivity, MapActivity::class.java).also {
                    it.putExtra(AddStoryActivity.EXTRA_TOKEN, userToken)
                    startActivity(it)
                }
                true
            }
            R.id.action_logout -> {
                showAlertDialog()
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