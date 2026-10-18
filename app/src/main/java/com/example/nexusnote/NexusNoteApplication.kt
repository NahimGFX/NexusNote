package com.example.nexusnote

import android.app.Application
import com.example.nexusnote.di.AppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NexusNoteApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}