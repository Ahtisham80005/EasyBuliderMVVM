package com.tradesk

import android.app.Application
import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp
class BuildzerApp : Application() {
    companion object{
        lateinit  var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        FirebaseApp.initializeApp(this)
        Places.initialize(applicationContext, "AIzaSyAweeG9yxU6nQulKdyN6nIIBtSPak1slfo")
    }
}