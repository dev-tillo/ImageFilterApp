package com.example.imagefilterapp.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imagefilterapp.databinding.ItemContainerFilterBinding
import com.example.imagefilterapp.listeners.SavedImagesListener
import java.io.File

class SavedImagesAdapter(
    private val savedImages: List<Pair<File, Bitmap>>,
    private val savedImagesListener: SavedImagesListener
) :
    RecyclerView.Adapter<SavedImagesAdapter.SavedImagesViewHolder>() {

    inner class SavedImagesViewHolder(val binding: ItemContainerFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedImagesViewHolder {
        return SavedImagesViewHolder(
            ItemContainerFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SavedImagesViewHolder, position: Int) {
        with(holder) {
            with(savedImages[position]) {
                binding.imageFilterPreview.setImageBitmap(second)

                binding.imageFilterPreview.setOnClickListener {
                    savedImagesListener.onImageClicked(first)
                }
            }
        }
    }

    override fun getItemCount(): Int = savedImages.size
}