package com.example.imagefilterapp.listeners

import com.example.imagefilterapp.data.ImageFilter

interface ImageFilterListener {
    fun onFilterSelectedListener(imageFilter: ImageFilter)
}