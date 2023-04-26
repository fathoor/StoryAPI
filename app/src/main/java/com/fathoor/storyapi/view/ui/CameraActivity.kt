package com.fathoor.storyapi.view.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.fathoor.storyapi.R
import com.fathoor.storyapi.databinding.ActivityCameraBinding
import com.fathoor.storyapi.view.helper.createFile

class CameraActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var orientationEventListener: OrientationEventListener? = null
    private var imageRotationDegrees: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageCapture = null
        orientationEventListener?.disable()
        ProcessCameraProvider.getInstance(this@CameraActivity).get().unbindAll()
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
        setupCamera()
    }

    private fun setupCamera() {
        setupOrientation()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.pvViewfinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@CameraActivity, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupOrientation() {
        orientationEventListener?.disable()
        orientationEventListener = object : OrientationEventListener(this@CameraActivity) {
            override fun onOrientationChanged(orientation: Int) {
                imageRotationDegrees = when (orientation) {
                    in 45..134 -> 270
                    in 135..224 -> 180
                    in 225..314 -> 90
                    else -> 0
                }
            }
        }
        orientationEventListener?.enable()
    }

    private fun setupAction() {
        binding.apply {
            ivCapture.setOnClickListener { capturePhoto() }
            ivSwitch.setOnClickListener { switchCamera() }
        }
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this@CameraActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    showToast(getString(R.string.camera_failed))
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("picture", photoFile)
                    intent.putExtra("isBackCamera", cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    intent.putExtra("imageRotationDegrees", imageRotationDegrees)

                    setResult(CAMERA_X_RESULT, intent)
                    finish()
                }
            }
        )
    }

    private fun switchCamera() {
        cameraSelector =
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA

        setupCamera()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@CameraActivity, message, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val CAMERA_X_RESULT = 200
    }
}