package com.tradesk.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.*
import javax.inject.Inject

//@Singleton
class PermissionFile @Inject constructor(private val context: Context) {

    fun checkLocStorgePermission(ctx: Context?): Boolean {
        val permissionCAMERA = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        val readStorage = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeStorage = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val mediaLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_MEDIA_LOCATION)

        val listPermissionsNeeded: MutableList<String> =
            ArrayList()
        if (readStorage != PackageManager.PERMISSION_GRANTED && writeStorage != PackageManager.PERMISSION_GRANTED && mediaLocationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    (ctx as Activity?)!!,
                    listPermissionsNeeded.toTypedArray(),
                    Constants.MULTI_LOC_STOR
                )
                return false
            }
        }
        return true
    }

    fun checkStorgePermission(ctx: Context?): Boolean {
        val readStorage = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeStorage = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: MutableList<String> =
            ArrayList()
        if (readStorage != PackageManager.PERMISSION_GRANTED && writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    (ctx as Activity?)!!,
                    listPermissionsNeeded.toTypedArray(), Constants.MULTI_STOR
                )
                return false
            }
        }
        return true
    }

    fun checklocationPermissions(ctx: Context?): Boolean {
        val permissionCAMERA = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val listPermissionsNeeded: MutableList<String> =
            ArrayList()
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    (ctx as FragmentActivity?)!!,
                    listPermissionsNeeded.toTypedArray(),
                    Constants.REQUEST_CODE_LOCATION
                )
                return false
            }
        }
        return true
    }

    fun checkMicrophonePermissions(ctx: Context?): Boolean {
        val permissionMICROPHONE = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        val listPermissionsNeeded: MutableList<String> =
            ArrayList()
        if (permissionMICROPHONE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    (ctx as FragmentActivity?)!!,
                    listPermissionsNeeded.toTypedArray(),
                    Constants.REQUEST_CODE_MICROPHONE
                )
                return false
            }
        }
        return true
    }

}