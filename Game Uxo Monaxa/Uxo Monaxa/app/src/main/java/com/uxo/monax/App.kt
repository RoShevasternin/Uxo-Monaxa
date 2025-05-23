package com.uxo.monax

import android.app.Application
import android.content.Context
import com.google.android.gms.games.PlayGamesSdk

lateinit var appContext: Context private set

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        PlayGamesSdk.initialize(this)
        appContext = applicationContext



    }

}