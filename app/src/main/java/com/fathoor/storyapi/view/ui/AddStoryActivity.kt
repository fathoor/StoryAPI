package com.fathoor.storyapi.view.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fathoor.storyapi.R
import com.fathoor.storyapi.databinding.ActivityAddStoryBinding
import com.fathoor.storyapi.view.helper.ViewModelFactory
import com.fathoor.storyapi.viewmodel.AddStoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddStoryBinding.inflate(layoutInflater) }
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private var userToken: String? = null
    private var getFile: File? = null
    private var launcherIntentCameraX: ActivityResultLauncher<Intent>? = null
    private var launcherIntentGallery: ActivityResultLauncher<Intent>? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var userLocation: Location? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                binding.apply {
                    btnCamera.isEnabled = true
                    btnGallery.isEnabled = true
                }
            } else {
                showToast(getString(R.string.camera_permission))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupView() {
        userToken = intent.getStringExtra(EXTRA_TOKEN) ?: ""
        setupAppBar()
        setupLocation()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this@AddStoryActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            addStoryViewModel.handleCameraActivityResult(it)
        }

        launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            addStoryViewModel.handleGalleryActivityResult(it)
        }
    }

    private fun setupAppBar() {
        binding.topAppBar.apply {
            @Suppress("DEPRECATION")
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setupLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@AddStoryActivity)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@AddStoryActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        } else {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    userLocation = it
                }
            }
        }
    }

    private fun setupViewModel() {
        addStoryViewModel.apply {
            file.observe(this@AddStoryActivity) { getFile = it }
            previewBitmap.observe(this@AddStoryActivity) { Glide.with(this@AddStoryActivity).load(it).into(binding.ivAddPhoto) }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnCamera.setOnClickListener { startCameraX() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadStory() }
            swLocation.setOnCheckedChangeListener { _, isChecked -> getUserLocation(isChecked)}
        }
    }

    private fun startCameraX() {
        Intent(this@AddStoryActivity, CameraActivity::class.java).also {
            launcherIntentCameraX?.launch(it)
        }
    }

    private fun startGallery() {
        Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
            launcherIntentGallery?.launch(it)
        }
    }

    private fun uploadStory() {
        if (validateUpload()) {
            addStoryViewModel.apply {
                userToken?.let { token ->
                    getFile?.let { photo ->
                        userStory(token, photo, binding.edAddDescription.text.toString())
                    }
                }.also {
                    isLoading.observe(this@AddStoryActivity) { showLoading(it) }
                    isUploaded.observe(this@AddStoryActivity) { if (it) navigateWithToken() }
                    error.observe(this@AddStoryActivity) { if (!it.isNullOrEmpty()) showToast(it) }
                }
            }
        }
    }

    private fun getUserLocation(isChecked: Boolean) {
        addStoryViewModel.apply {
            if (isChecked) {
                if (userLocation != null) {
                    userLocation?.let { location ->
                        latitude.value = location.latitude
                        longitude.value = location.longitude
                    }
                } else {
                    latitude.value = DEFAULT_LAT
                    longitude.value = DEFAULT_LON
                }
            } else {
                latitude.value = null
                longitude.value = null
            }
        }
    }

    private fun validateUpload(): Boolean = binding.run {
        when {
            ivAddPhoto.drawable == null -> {
                showToast(getString(R.string.camera_upload_image))
                false
            }
            edAddDescription.text.toString().isEmpty() -> {
                showToast(getString(R.string.camera_upload_description))
                false
            }
            else -> true
        }
    }

    private fun navigateWithToken() {
        Intent(this@AddStoryActivity, MainActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(MainActivity.EXTRA_TOKEN, userToken)
            startActivity(intent)
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@AddStoryActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        binding.cpiAdd.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION")
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val DEFAULT_LAT = -6.8957643
        private const val DEFAULT_LON = 107.6338462
    }
}