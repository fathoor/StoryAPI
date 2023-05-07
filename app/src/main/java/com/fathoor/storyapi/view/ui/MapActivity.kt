package com.fathoor.storyapi.view.ui

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.fathoor.storyapi.R
import com.fathoor.storyapi.databinding.ActivityMapBinding
import com.fathoor.storyapi.model.remote.response.Story
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private val mapViewModel by viewModels<MapViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var userToken: String? = null
    private var storyList: List<Story>? = null
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        userToken = intent.getStringExtra(EXTRA_TOKEN) ?: ""
        setupAppBar()
        setupMap()
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            @Suppress("DEPRECATION")
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapActivity)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }

        userToken?.let { token ->
            mapViewModel.apply {
                userStoryMap(token).also {
                    story.observe(this@MapActivity) { storyList ->
                        storyList.forEach { story ->
                            if (story.lat != null && story.lon != null) {
                                val latLng = LatLng(story.lat.toDouble(), story.lon.toDouble())
                                map.addMarker(
                                    MarkerOptions().position(latLng).title(story.name)
                                        .snippet(story.description)
                                )
                            }
                        }
                        isLoading.observe(this@MapActivity) { showLoading(it) }
                        error.observe(this@MapActivity) { if (!it.isNullOrEmpty()) showToast(it) }
                    }
                }
            }
        }

        val indonesiaCenter = LatLng(-2.548926, 118.014863)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(indonesiaCenter, 3f))

        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapActivity, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MapActivity, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showLoading(state: Boolean) {
        binding.cpiMap.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        private const val TAG = "MapActivity"
    }
}