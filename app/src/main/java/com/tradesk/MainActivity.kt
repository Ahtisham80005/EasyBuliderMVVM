package com.tradesk

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.View
import androidx.databinding.DataBindingUtil
import com.tradesk.activity.auth.LoginActivity
import com.tradesk.databinding.ActivityMainBinding
import com.tradesk.databinding.ActivitySignupBinding
import com.tradesk.filemanager.checkStoragePermission
import com.tradesk.filemanager.requestStoragePermission
import com.tradesk.fragment.CalendarFragment
import com.tradesk.fragment.HomeFragment
import com.tradesk.fragment.JobsFragment
import com.tradesk.fragment.ProfileFragment
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.changeMainFragment
import com.tradesk.util.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        var page_clicked = "0"
    }
    var lastclicked = 0L
    lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var mPrefs: PreferenceHelper
    @Inject
    lateinit var permissionFile: PermissionFile
    private var hasPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        if (mPrefs.getKeyValue(PreferenceConstants.LANGUAGE).isEmpty()) {
            mPrefs.setKeyValue(PreferenceConstants.LANGUAGE, "en")
        }

        hasPermission = checkStoragePermission(this)
        if (!hasPermission)
        {
            requestStoragePermission(this)
        }

//        val extras = intent.extras
//        if (extras != null)
//        {
//            if (extras.containsKey("notification")) {
//                // extract the extra-data in the Notification
//                changeMainFragment(mainContainer, NotificationFragment())
//                binding.bottomNavigationView.selectedItemId = R.id.menu_notification;
//            } else changeMainFragment(mainContainer, homeFragment)
//        } else {
            changeMainFragment(binding.mainContainer, HomeFragment())
//        }
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            if (SystemClock.elapsedRealtime() - lastclicked > 500) {
//                if (it.itemId == bottomNavigationView.selectedItemId) return@setOnNavigationItemSelectedListener false
                when (it.itemId) {
                    R.id.menu_home -> {
                        page_clicked = "1"
                        changeMainFragment(binding.mainContainer, HomeFragment())
                    }
                    R.id.menu_jobs -> {
                        page_clicked = "2"
                        changeMainFragment(binding.mainContainer, JobsFragment())
                    }
                    R.id.menu_calendar -> {
                        page_clicked = "3"
                        changeMainFragment(binding.mainContainer, CalendarFragment())
                    }

                    R.id.menu_notification -> {
                        page_clicked = "4"
//                        changeMainFragment(mainContainer, NotificationFragment())
                    }

                    R.id.menu_profile -> {
                        page_clicked = "5"
                        changeMainFragment(binding.mainContainer, ProfileFragment())
                    }
                }
                lastclicked = SystemClock.elapsedRealtime()
                true
            } else {
                false
            }
        }
    }

    fun onLogout(message: String) {
        toast("Autherization failed")
        mPrefs.logoutUser()
        Intent(this@MainActivity, LoginActivity::class.java).putExtra(
            "logout",
            "true").apply {
            startActivity(this)
        }
        finishAffinity()
    }
}