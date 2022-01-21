package com.example.imagefilterapp.activities.filteredimage

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imagefilterapp.R
import com.example.imagefilterapp.activities.edit.EditImage
import com.example.imagefilterapp.databinding.ActivityFilteredImageBinding
import org.koin.android.ext.android.bind

class FilteredImage : AppCompatActivity() {

    private lateinit var fileUri: Uri
    private lateinit var binding: ActivityFilteredImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilteredImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        displayFilteredImage()
        setListener()
    }

    private fun displayFilteredImage() {
        intent.getParcelableExtra<Uri>(EditImage.KEY_FILTERED_IMAGE_URI)?.let {
            fileUri = it
            binding.imageFilterPreview.setImageURI(it)
        }
    }

    private fun setListener() {
        binding.fabShare.setOnClickListener {
            with(Intent(Intent.ACTION_SEND)) {
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
                startActivity(this)
            }
        }
        binding.left.setOnClickListener {
            onBackPressed()
        }
    }
}