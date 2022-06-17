package com.oliversolutions.dev.bloodpressurediary

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi

class App: Application() {
    companion object {
        lateinit var prefs: Prefs
        lateinit var instance: App
            private set
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = Prefs(applicationContext)
    }
}
