/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tradesk.filemanager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Browser
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.tradesk.BuildConfig
import com.tradesk.R
import java.io.File

private const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.provider"

fun getMimeType(url: String): String {
    val ext = MimeTypeMap.getFileExtensionFromUrl(url)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "text/plain"
}

fun getFilesList(selectedItem: File): List<File> {
    val rawFilesList = selectedItem.listFiles()?.filter { !it.isHidden }

    return if (selectedItem == Environment.getExternalStorageDirectory()) {
        rawFilesList?.toList() ?: listOf()
    } else {
        listOf(selectedItem.parentFile) + (rawFilesList?.toList() ?: listOf())
    }
}

fun renderParentLink(activity: AppCompatActivity): String {
    return activity.getString(R.string.go_parent_label)
}

fun renderItem(activity: AppCompatActivity, file: File): String {
    return if (file.isDirectory) {
        activity.getString(R.string.folder_item, file.name)
    } else {
        activity.getString(R.string.file_item, file.name)
    }
}


fun passResult(activity: AppCompatActivity, selectedItem: File) {

    activity.setResult(Activity.RESULT_OK,
        Intent().putExtra("_data", selectedItem.absolutePath ?: selectedItem.path))
    activity.finish()
}

fun openApp(activity: AppCompatActivity, selectedItem: File) {
    // Get URI and MIME type of file

    val uri = FileProvider.getUriForFile(activity.applicationContext, AUTHORITY, selectedItem)
    val mime: String = getMimeType(uri.toString())

    // Open file with user selected app
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(uri, mime)
    return activity.startActivity(intent)
}

fun openApp(activity: AppCompatActivity, url: String) {
    // Get URI and MIME type of file
    // Open file with user selected app
    val ext = MimeTypeMap.getFileExtensionFromUrl(url)

    val isDoc=ext.equals("pdf", true) || ext.equals("docx", true) || ext.equals("doc", true)
    if (isDoc) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome");
        return activity.startActivity(intent)
    }
    else{
        activity.startActivity(Intent(activity,FullViewActivity::class.java).putExtra("url_file",url))
    }
}

fun openAppPermits(activity: AppCompatActivity, url: String,id:String) {
    // Get URI and MIME type of file
    // Open file with user selected app
    val ext = MimeTypeMap.getFileExtensionFromUrl(url)

    val isDoc=ext.equals("pdf", true) || ext.equals("docx", true) || ext.equals("doc", true)
    if (isDoc) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome");
        return activity.startActivity(intent)
    }
    else{
        activity.startActivity(Intent(activity,FullViewActivity::class.java).putExtra("url_file",url))
    }
}
