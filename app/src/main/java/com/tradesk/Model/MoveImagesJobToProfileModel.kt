package com.tradesk.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoveImagesJobToProfileModel(
    val job_id: String,
    val images_urls: List<String>,
    val imageIds: List<String>,
    val index_of_image: Int
):Parcelable
