package com.example.imagefilterapp.dependecyinjection

import com.example.imagefilterapp.repositories.EditImageRepository
import com.example.imagefilterapp.repositories.EditImageRepositoryImpl
import com.example.imagefilterapp.repositories.SavedImagesRepository
import com.example.imagefilterapp.repositories.SavedImagesRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<EditImageRepository> { EditImageRepositoryImpl(androidContext()) }
    factory<SavedImagesRepository> { SavedImagesRepositoryImpl(androidContext())}
}