package com.tradesk.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoveImagesUserToUser(
    val to: String,
    val from_index: Int,
    val to_index: Int,
    val urls: List<String>,
):Parcelable
