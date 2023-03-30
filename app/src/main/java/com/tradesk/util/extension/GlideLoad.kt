package com.tradesk.util.extension

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.widget.ImageView
import com.tradesk.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


internal fun ImageView.loadImageRadius(img: Any, radius: Int = 20) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img).apply(
                RequestOptions().transform(RoundedCorners(radius)).placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
            ).into(this)
        }
    }
}

internal fun ImageView.loadUserImage(img: Any) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img)
                .apply(RequestOptions.circleCropTransform().error(R.mipmap.ic_launcher)).into(this)
        }
    }
}


internal fun ImageView.loadOrigImage(img: Any) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img).apply(RequestOptions().error(R.mipmap.ic_launcher))
                .into(this)
        }
    }
}

internal fun ImageView.loadWallImage(img: Any) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img).into(this)
        }
    }
}

internal fun ImageView.makeGrey() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)
    this.colorFilter = ColorMatrixColorFilter(matrix)
}

fun ImageView.loadCenterFitImage(imgPath: String?) {
    Glide.with(this)
        .applyDefaultRequestOptions(
            RequestOptions().fitCenter().diskCacheStrategy(DiskCacheStrategy.DATA)
        )
        .load(imgPath)
        .into(this)
}