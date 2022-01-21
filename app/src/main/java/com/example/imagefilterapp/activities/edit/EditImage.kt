package com.example.imagefilterapp.activities.edit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.imagefilterapp.activities.filteredimage.FilteredImage
import com.example.imagefilterapp.activities.main.MainActivity
import com.example.imagefilterapp.adapters.ImageFiltersAdapter
import com.example.imagefilterapp.data.ImageFilter
import com.example.imagefilterapp.databinding.ActivityEditImageBinding
import com.example.imagefilterapp.listeners.ImageFilterListener
import com.example.imagefilterapp.utils.displayToast
import com.example.imagefilterapp.utils.show
import com.example.imagefilterapp.viewmodels.EditImageViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditImage : AppCompatActivity(), ImageFilterListener {

    companion object {
        const val KEY_FILTERED_IMAGE_URI = "filteredImageUri"
    }

    private lateinit var binding: ActivityEditImageBinding
    private val viewModele: EditImageViewModel by viewModel()
    private lateinit var pguImage: GPUImage
    private lateinit var originalBitmap: Bitmap
    private var filteredBitmap = MutableLiveData<Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setlistener()
        displayImagePreview()
        setupObservers()
        prepareImagePreview()
    }

    private fun setupObservers() {
        viewModele.imagePreviewUIState.observe(this, {
            val dataState = it ?: return@observe
            binding.progresbar.visibility =
                if (dataState.isLoadingBoolean) View.VISIBLE else View.GONE
            dataState.bitmap?.let { bitmap ->
                originalBitmap = bitmap
                filteredBitmap.value = bitmap

                with(originalBitmap) {
                    pguImage.setImage(this)
                    binding.imagePreview.show()
                    viewModele.loadImageFilters(this)
                }

            } ?: run {
                dataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })

        viewModele.imageFiltersUiState.observe(this, {
            val imagePreviewDataState = it ?: return@observe
            binding.progressBarImageFilter.visibility =
                if (imagePreviewDataState.isLoadingBoolean) View.VISIBLE else View.GONE
            imagePreviewDataState.imageFilter?.let { image ->
                ImageFiltersAdapter(image, this).also { adapter ->
                    binding.rvc.adapter = adapter
                }
            } ?: run {
                imagePreviewDataState.error?.let { error ->
                    displayToast(error)
                }
            }
        })

        filteredBitmap.observe(this, { bitmap ->
            binding.imagePreview.setImageBitmap(bitmap)
        })

        viewModele.saveFilteredImageUIState.observe(this, {
            val saveFilteredImageDataState = it ?: return@observe
            if (saveFilteredImageDataState.isLoadingBoolean) {
                binding.save.visibility = View.GONE
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
                binding.save.visibility = View.VISIBLE
            }
            saveFilteredImageDataState.uri?.let { savedimageUri ->
                Intent(
                    applicationContext,
                    FilteredImage::class.java
                ).also { filteredImage ->
                    filteredImage.putExtra(KEY_FILTERED_IMAGE_URI, savedimageUri)
                    startActivity(filteredImage)
                }
            } ?: run {
                saveFilteredImageDataState.error?.let {
                    displayToast(it.toString())
                }
            }
        })
    }

    private fun displayImagePreview() {
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)?.let { imageUri ->
            val openInputStream = contentResolver.openInputStream(imageUri)
            val decodeStream = BitmapFactory.decodeStream(openInputStream)
            binding.imagePreview.setImageBitmap(decodeStream)
            binding.imagePreview.visibility = View.VISIBLE
        }
    }

    private fun prepareImagePreview() {
        pguImage = GPUImage(applicationContext)
        intent.getParcelableExtra<Uri>(MainActivity.KEY_IMAGE_URI)?.let { imageUri ->
            viewModele.prepareImagePreview(imageUri)
        }
    }

    private fun setlistener() {
        binding.left.setOnClickListener {
            onBackPressed()
        }

        binding.save.setOnClickListener {
            filteredBitmap.value?.let { bitmap ->
                viewModele.saveFilteredImage(bitmap)
            }
        }

        binding.imagePreview.setOnLongClickListener {
            binding.imagePreview.setImageBitmap(originalBitmap)
            return@setOnLongClickListener false
        }

        binding.imagePreview.setOnClickListener {
            binding.imagePreview.setImageBitmap(filteredBitmap.value)
        }

    }

    override fun onFilterSelectedListener(imageFilter: ImageFilter) {
        with(imageFilter) {
            with(pguImage) {
                setFilter(filter)
                filteredBitmap.value = bitmapWithFilterApplied
            }
        }
    }
}