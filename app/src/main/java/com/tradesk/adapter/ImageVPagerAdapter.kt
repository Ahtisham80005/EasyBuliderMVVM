package com.tradesk.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.makeramen.roundedimageview.RoundedImageView
import com.socialgalaxyApp.util.extension.loadWallRound
import com.tradesk.BuildzerApp
import com.tradesk.Model.AdditionalImageLeadDetail
import com.tradesk.R
import com.tradesk.activity.ImageActivity
import com.tradesk.databinding.RowItemCustomerslistBinding
import com.tradesk.util.Constants

class ImageVPagerAdapter (val context: Context,
                          private val images: MutableList<AdditionalImageLeadDetail>,
                          val onClick: () -> Unit
) : PagerAdapter(){

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

        imageView.loadWallRound(images[position].image)
        imageView.setOnClickListener {
            onClick.invoke()
            context.startActivity(
                Intent(context, ImageActivity::class.java).
                putExtra("position",position)
                    .putExtra("image",images[position].image)
                    .putParcelableArrayListExtra("imagelist", images as ArrayList<AdditionalImageLeadDetail>))
        }

//        startActivity(Intent(this, ImageActivity::class.java).putExtra("image",images[position].image)
//        Glide.with(container.getContext()).load(images[position])
//            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(imageView)

        /* Picasso.with(container.getContext())
                .load(images.get(position))
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);*/container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}