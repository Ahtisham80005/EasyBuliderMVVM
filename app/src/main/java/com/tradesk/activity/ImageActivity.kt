package com.tradesk.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Model.AdditionalImageImageClient
import com.tradesk.Model.AdditionalImageLeadDetail
import com.tradesk.R
import com.tradesk.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {
    lateinit var binding:ActivityImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_image)

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
//        val photoView = findViewById<View>(com.buildzer.R.id.photo_view) as PhotoView
//        photoView.loadWallImage(intent.getStringExtra("image").toString())

        if (intent.getStringExtra("expense").equals("Expenses")){
            binding.viewPager.visibility= View.GONE
            binding.photoView.visibility= View.VISIBLE
            binding.photoView.loadWallImage(intent.getStringExtra("image").toString())
        }
        else{
            binding.viewPager.visibility= View.VISIBLE
            binding.photoView.visibility= View.GONE
            if (intent.hasExtra("title"))
            {
                if (intent.getStringExtra("title").equals("Job Images"))
                {
                    val imagelist = intent.getParcelableArrayListExtra<AdditionalImageImageClient>("imagelist") as ArrayList<AdditionalImageImageClient>
                    val imagePostion=intent.getIntExtra("position",0)
                    viewPager.adapter = JobImagesPagerAdapter(imagelist)
                    viewPager.setCurrentItem(imagePostion)
                    Toast.makeText(applicationContext,"1", Toast.LENGTH_SHORT).show()
                }else if (intent.getStringExtra("title").equals("Permits")){
                    val imagelist = intent.getParcelableArrayListExtra<AdditionalImageLeadDetail>("imagelist") as ArrayList<AdditionalImageLeadDetail>
                    viewPager.adapter = PermitsPagerAdapter(imagelist)
                    Toast.makeText(applicationContext,"2", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"3", Toast.LENGTH_SHORT).show()
                    val imagelistDocs = intent.getStringArrayListExtra("imagelist") as ArrayList<String>
                    val imagePostion=intent.getIntExtra("position",0)
                    viewPager.adapter = DocsPagerAdapter(imagelistDocs)
                    viewPager.setCurrentItem(imagePostion)
                }
            }else{
                Toast.makeText(applicationContext,"4", Toast.LENGTH_SHORT).show()
                val imagelist = intent.getParcelableArrayListExtra<AdditionalImageLeadDetail>("imagelist") as ArrayList<AdditionalImageLeadDetail>
                viewPager.adapter = SamplePagerAdapter(imagelist)
                val imagePostion=intent.getIntExtra("position",0)
                viewPager.setCurrentItem(imagePostion)
            }
        }
        binding.mIvBack.setOnClickListener { finish() }
    }

    internal class JobImagesPagerAdapter(private val images: MutableList<AdditionalImageImageClient>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position].image)
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }

    internal class SamplePagerAdapter(private val images: MutableList<AdditionalImageLeadDetail>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position].image)
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }

    internal class DocsPagerAdapter(private val images: MutableList<String>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position])
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }

    internal class PermitsPagerAdapter(private val images: MutableList<AdditionalImageLeadDetail>) : PagerAdapter() {
        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.loadWallImage(images[position].image)
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }


    }
}