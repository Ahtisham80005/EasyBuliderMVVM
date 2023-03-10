package com.tradesk.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.tradesk.MainActivity
import com.tradesk.R
import com.tradesk.activity.auth.LoginActivity
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var mPrefs: PreferenceHelper

    var SPLASH_TIME_OUT = 2000
    val handler by lazy { Handler() }
    val runnable by lazy {
        Runnable {
            if (mPrefs.isUserLoggedIn(PreferenceConstants.USER_LOGGED_IN)) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler.postDelayed(runnable, SPLASH_TIME_OUT.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}