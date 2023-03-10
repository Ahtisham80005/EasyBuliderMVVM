package com.tradesk.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoveAdditionalImagesModel(
    val from_job_id: String,
    val to_job_id: String,
    val image_id: List<String>,
    val image_url: List<String>
):Parcelable
