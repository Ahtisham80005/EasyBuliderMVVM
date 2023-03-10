package com.tradesk.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoveImagesUserToJob(
    val to: String,
    val from_index: Int,
    val jobId: String,
    val urls: List<String>,
):Parcelable
