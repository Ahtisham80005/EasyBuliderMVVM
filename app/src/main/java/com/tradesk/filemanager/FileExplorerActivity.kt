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

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tradesk.R
import com.tradesk.databinding.ActivityFileExplorerBinding
//import kotlinx.android.synthetic.main.activity_file_explorer.*
import java.io.File

const val MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1
const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2

class FileExplorerActivity : AppCompatActivity() {
    private var hasPermission = false
    private lateinit var currentDirectory: File
    private lateinit var filesList: List<File>
    private lateinit var adapter: ArrayAdapter<String>
    lateinit var binding:ActivityFileExplorerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_file_explorer)
        setupUi()
    }

    override fun onResume() {
        super.onResume()

        hasPermission = checkStoragePermission(this)
        if (hasPermission) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q)
            {
                if (!Environment.isExternalStorageLegacy()) {
                    binding.rationaleView.visibility = View.GONE
                    binding.legacyStorageView.visibility = View.VISIBLE
                    return
                }
            }
            binding.rationaleView.visibility = View.GONE
            binding.filesTreeView.visibility = View.VISIBLE
            // TODO: Use getStorageDirectory instead https://developer.android.com/reference/android/os/Environment.html#getStorageDirectory()
            open(getExternalStorageDirectory())
        } else {
            binding.rationaleView.visibility = View.VISIBLE
            binding.filesTreeView.visibility = View.GONE
        }
    }

    private fun setupUi() {
//        toolbar.setOnMenuItemClickListener {
//            startActivity(Intent(this, SettingsActivity::class.java))
//            true
//        }

        binding.permissionButton.setOnClickListener {
            requestStoragePermission(this)
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        binding.filesTreeView.adapter = adapter
        binding.filesTreeView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = filesList[position]
            open(selectedItem)
        }
    }

    private fun open(selectedItem: File) {
        if (selectedItem.isFile) {
            return passResult(this, selectedItem)
        }

        currentDirectory = selectedItem
        filesList = getFilesList(currentDirectory).filter { it.isDirectory||(it.isFile&&(it.extension.equals("docx")||it.extension.equals("doc")||it.extension.equals("pdf"))) }
        adapter.clear()
        adapter.addAll(filesList.map {
            if (it.path == selectedItem.parentFile.path) {
                renderParentLink(this)
            } else {
                renderItem(this, it)
            }
        })
        adapter.notifyDataSetChanged()
    }
}
