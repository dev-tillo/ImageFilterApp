package com.example.imagefilterapp.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imagefilterapp.data.ImageFilter
import com.example.imagefilterapp.repositories.EditImageRepository
import com.example.imagefilterapp.utils.Coroutines

class EditImageViewModel(private var editImageRepository: EditImageRepository) : ViewModel() {

    //region::Prepare image preview

    private val imagePreviewDataState = MutableLiveData<ImagePreviewDataState>()
    val imagePreviewUIState: LiveData<ImagePreviewDataState> get() = imagePreviewDataState

    fun prepareImagePreview(imageUri: Uri) {
        Coroutines.io {
            runCatching {
                emitImagePreviewUiState(isLoadingBoolean = true)
                editImageRepository.prepareImagePreview(imageUri)
            }.onSuccess { bitmap ->
                if (bitmap != null) {
                    emitImagePreviewUiState(bitmap = bitmap)
                } else {
                    emitImagePreviewUiState(error = "Unable to prepare image ")
                }
            }.onFailure {
                emitImagePreviewUiState(error = it.message.toString())
            }
        }
    }

    private fun emitImagePreviewUiState(
        isLoadingBoolean: Boolean = false,
        bitmap: Bitmap? = null,
        error: String? = null
    ) {
        val dataState = ImagePreviewDataState(isLoadingBoolean, bitmap, error)
        imagePreviewDataState.postValue(dataState)
    }

    data class ImagePreviewDataState(
        val isLoadingBoolean: Boolean,
        val bitmap: Bitmap?,
        val error: String?
    )
    //endregion

    //region:: Load image filters

    private var imageFiltersDataState = MutableLiveData<ImageFilterDataState>()
    val imageFiltersUiState: LiveData<ImageFilterDataState> get() = imageFiltersDataState

    fun loadImageFilters(originalImage: Bitmap) {
        Coroutines.io {
            runCatching {
                emitImageFiltersUiState(isLoadingBoolean = true)
                editImageRepository.getImageFilters(getPreviewImage(originalImage))
            }.onSuccess { imageFilters ->
                emitImageFiltersUiState(imageFilter = imageFilters)
            }.onFailure {
                emitImageFiltersUiState(error = it.message.toString())
            }
        }
    }

    private fun getPreviewImage(originalImage: Bitmap): Bitmap {
        return runCatching {
            val previewWidth = 150
            val previewHeight = originalImage.height * previewWidth / originalImage.width
            Bitmap.createScaledBitmap(originalImage, previewWidth, previewHeight, false)
        }.getOrDefault(originalImage)
    }

    private fun emitImageFiltersUiState(
        isLoadingBoolean: Boolean = false,
        imageFilter: List<ImageFilter>? = null,
        error: String? = null
    ) {
        val dataState = ImageFilterDataState(isLoadingBoolean, imageFilter, error)
        imageFiltersDataState.postValue(dataState)
    }

    data class ImageFilterDataState(
        val isLoadingBoolean: Boolean,
        val imageFilter: List<ImageFilter>?,
        val error: String?
    )

    //endregion

    //region:: Save filtered Image
    private val saveFilteredImageDataState = MutableLiveData<SaveFilteredImageDataState>()
    val saveFilteredImageUIState: LiveData<SaveFilteredImageDataState>
        get() = saveFilteredImageDataState

    fun saveFilteredImage(filtered: Bitmap) {
        Coroutines.io {
            runCatching {
                emitSaveFilteredImageUiState(isLoadingBoolean = true)
                editImageRepository.saveFilteredImage(filtered)
            }.onSuccess { savedImageUri ->
                emitSaveFilteredImageUiState(uri = savedImageUri)
            }.onFailure {
                emitSaveFilteredImageUiState(error = it.message.toString())
            }
        }
    }

    private fun emitSaveFilteredImageUiState(
        isLoadingBoolean: Boolean = false,
        uri: Uri? = null,
        error: String? = null
    ) {
        val dataState = SaveFilteredImageDataState(isLoadingBoolean, uri, error)
        saveFilteredImageDataState.postValue(dataState)
    }

    data class SaveFilteredImageDataState(
        val isLoadingBoolean: Boolean,
        val uri: Uri?,
        val error: String?
    )
    //endregion

}