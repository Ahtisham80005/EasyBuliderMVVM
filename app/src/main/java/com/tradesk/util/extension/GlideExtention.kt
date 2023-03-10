package com.socialgalaxyApp.util.extension

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target


internal fun ImageView.loadGif(img: Any) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).asGif().load(img).diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(RoundedCorners(30))/*.placeholder(place)*/
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).centerCrop().into(this)
        }
    }
}

internal fun ImageView.loadUserImage(img: Any) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img).diskCacheStrategy(DiskCacheStrategy.ALL)/*.placeholder(
                R.drawable.ic_grey_nodp)*/
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).centerCrop().into(this)
        }
    }
}

internal fun ImageView.loadWallImage(img: Any/*,place:Int=R.drawable.ic_grey_nodp*/) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)/*.placeholder(place)*/
//                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .centerCrop().into(this)

        }
    }
}

internal fun ImageView.loadWallRound(img: Any/*,place:Int=R.drawable.ic_grey_nodp*/) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)/*.placeholder(place)*/
                .transform(RoundedCorners(10)).override(
                    Target.SIZE_ORIGINAL,
                    Target.SIZE_ORIGINAL
                ).centerCrop().into(this)
        }
    }
}

internal fun ImageView.loadLikeImage(img: Any) {
    when (img) {
        is String, is Int, is Uri -> {
            Glide.with(this.context).load(img).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )./*placeholder(R.drawable.ic_grey_nodp).*/override(
                Target.SIZE_ORIGINAL,
                Target.SIZE_ORIGINAL
            ).into(this)
        }
    }
}

internal fun ImageView.makeGrey() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)
    this.colorFilter = ColorMatrixColorFilter(matrix)
}