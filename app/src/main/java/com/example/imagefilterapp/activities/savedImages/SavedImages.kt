package com.example.imagefilterapp.activities.savedImages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import com.example.imagefilterapp.activities.edit.EditImage
import com.example.imagefilterapp.activities.filteredimage.FilteredImage
import com.example.imagefilterapp.adapters.SavedImagesAdapter
import com.example.imagefilterapp.databinding.ActivitySavedImagesBinding
import com.example.imagefilterapp.listeners.SavedImagesListener
import com.example.imagefilterapp.utils.displayToast
import com.example.imagefilterapp.viewmodels.SavedImagesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class SavedImages : AppCompatActivity(), SavedImagesListener {

    private lateinit var binding: ActivitySavedImagesBinding
    private val viewModel: SavedImagesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObserver()
        setListeners()
        viewModel.loadSavedImages()
    }

    private fun setupObserver() {
        viewModel.savedImagesUiState.observe(this, {
            val savedImagesDataState = it ?: return@observe
            binding.savedimagePorgresbarr.visibility =
                if (savedImagesDataState.isLoading) View.VISIBLE else View.GONE
            savedImagesDataState.savedImages?.let { savedImages ->
                SavedImagesAdapter(savedImages, this).also { adpter ->
                    with(binding.rvc) {
                        this.adapter = adapter
                        visibility = View.VISIBLE
                    }
                }
            } ?: run {
                savedImagesDataState.error?.let {
                    displayToast(it)
                }
            }
        })
    }

    private fun setListeners() {
        binding.left.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onImageClicked(file: File) {
        val fileUri =
            FileProvider.getUriForFile(
                applicationContext, "${packageName}.provider", file)
        Intent(
            applicationContext,
            FilteredImage::class.java
        ).also {
            it.putExtra(EditImage.KEY_FILTERED_IMAGE_URI, fileUri)
            startActivity(it)
        }
    }
}