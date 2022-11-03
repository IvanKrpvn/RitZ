package com.example.viewpagerexperiment

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper

lateinit var appContext: Context

class App :Application() {

    companion object {
        lateinit var handler: Handler
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        handler = Handler(Looper.myLooper()!!)
    }

}