package com.fathoor.storyapi.viewmodel

import android.app.Activity.RESULT_OK
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fathoor.storyapi.model.repository.StoryRepository
import com.fathoor.storyapi.view.helper.reduceFileImage
import com.fathoor.storyapi.view.helper.rotateFile
import com.fathoor.storyapi.view.helper.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository, private val application: Application) : ViewModel() {
    private val _file = MutableLiveData<File>()
    val file: LiveData<File> = _file

    private val _previewBitmap = MutableLiveData<Bitmap>()
    val previewBitmap: LiveData<Bitmap> = _previewBitmap

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUploaded = MutableLiveData<Boolean>()
    val isUploaded: LiveData<Boolean> = _isUploaded

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun handleCameraActivityResult(result: ActivityResult) {
        if (result.resultCode == CAMERA_X_SUCCESS) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let {
                rotateFile(it, isBackCamera)
                _file.value = it
                _previewBitmap.value = BitmapFactory.decodeFile(it.path)
            }
        }
    }

    fun handleGalleryActivityResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let {
                val myFile = uriToFile(it, application)
                _file.value = myFile
                _previewBitmap.value = BitmapFactory.decodeFile(myFile.path)
            }
        }
    }

    fun userStory(token: String, photo: File, description: String) = viewModelScope.launch {
        _isLoading.value = true
        _isUploaded.value = false
        _error.value = null

        val photoFile = reduceFileImage(photo)
        val photoDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImage = photoFile.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData("photo", photoFile.name, requestImage)

        try {
            repository.userStory(token, imageMultipart, photoDescription).let {
                _isLoading.value = false
                _isUploaded.value = true
            }
        } catch (e: Exception) {
            _isLoading.value = false
            _isUploaded.value = false
            if (e is HttpException) {
                if (e.code() == 401) {
                    _error.value = ERROR_TOKEN
                } else {
                    _error.value = "HTTP Error ${e.code()}"
                }
            }
            Log.e(TAG, "userStory: ${e.message}")
        }
    }

    private companion object {
        const val CAMERA_X_SUCCESS = 200

        const val TAG = "AddStoryViewModel"
        const val ERROR_TOKEN = "You are not logged in!"
    }
}