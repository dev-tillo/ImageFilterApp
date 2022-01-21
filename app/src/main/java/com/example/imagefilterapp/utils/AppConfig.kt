package com.example.imagefilterapp.utils

import android.app.Application
import com.example.imagefilterapp.dependecyinjection.repositoryModule
import com.example.imagefilterapp.dependecyinjection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class AppConfig : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppConfig)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}