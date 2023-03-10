package com.tradesk.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoveImagesInFolderModel(
    val job_id: String,
    val images_urls: List<String>,
    val imageIds: List<String>,
    val folder_name: String
):Parcelable
