package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.tradesk.R

class ImagesPagerAdapter(
    private val  context: Context,
    private val images: MutableList<String>,
    val onClick: () -> Unit
) : PagerAdapter() {


    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o === view
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(container.context).inflate(R.layout.slidingimages_layout, null)
        val imageView = view.findViewById<RoundedImageView>(R.id.image)
        Glide.with(context).load(images[position]).into(imageView);
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}