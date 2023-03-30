package com.tradesk.filemanager

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.tradesk.R

import com.google.android.material.snackbar.Snackbar
import com.tradesk.databinding.ActivityFullViewBinding
import com.tradesk.util.extension.loadOrigImage

class FullViewActivity : AppCompatActivity() {
    val url_file by lazy { intent.getStringExtra("url_file")?:"" }
    lateinit var binding:ActivityFullViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_full_view)
        binding.mIvBack.setOnClickListener { onBackPressed() }
        if (isInternetConnected().not()||url_file.isEmpty()) onBackPressed()
        binding.ivWallpaper.apply {
           loadOrigImage(url_file)
        }
    }

    fun isInternetConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnectedOrConnecting)
            return true
        else {
            Snackbar.make(findViewById<View>(android.R.id.content), "You are Offline!", 2000).show()
            return false
        }
    }
}
