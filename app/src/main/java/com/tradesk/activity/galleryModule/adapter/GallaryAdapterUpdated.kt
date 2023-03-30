package com.tradesk.activity.galleryModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.AdditionalImageLeadDetail
import com.tradesk.Model.CheckModel
import com.tradesk.R
import com.tradesk.databinding.RowItemGalleryUpdatedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GallaryAdapterUpdated  (context: Context,
                              val listener: SingleListCLickListener,
                              var mImages: MutableList<AdditionalImageLeadDetail>,
                              val longClickListener: LongClickListener,
                              var customCheckBoxListener: CustomCheckBoxListener,
                              val unselectCheckBoxListener: UnselectCheckBoxListener,
                              var checkboxVisibility:Boolean,
                              var allCheckBoxSelect:Boolean,
                              var mcheckBoxModelList: MutableList<CheckModel>
) : RecyclerView.Adapter<GallaryAdapterUpdated.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemGalleryUpdatedBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemGalleryUpdatedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_gallery_updated, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =   mImages.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            if(checkboxVisibility)
            {
                binding.mCheckBox.visibility=View.VISIBLE
            }
            if(allCheckBoxSelect)
            {
                binding.mCheckBox.isChecked = true
                binding.mCheckBox.isClickable = false
            }
            //in some cases, it will prevent unwanted situations
            binding.mCheckBox.setOnCheckedChangeListener(null)
            binding.mCheckBox.tag=position
            binding.mCheckBox.isChecked=mcheckBoxModelList.get(position).check
            binding.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
                mcheckBoxModelList.get(position).check=isChecked
                if (isChecked) {
                    customCheckBoxListener.onCheckBoxClick(position)
                } else {
                    unselectCheckBoxListener.onCheckBoxUnCheckClick(0, position)
                }
            }

            val ext = MimeTypeMap.getFileExtensionFromUrl(mImages[position].image)
            val isDoc = ext.equals("pdf", true) || ext.equals("docx", true) || ext.equals("doc", true)
            binding.mIvPic.isVisible = (isDoc).not()
            binding.tvDocument.isVisible = isDoc

            CoroutineScope(Dispatchers.Main).launch {
                Picasso.get()
                    .load(mImages[position].image)
                    .resize(200,200)
                    .centerCrop()
                    .into(binding.mIvPic, object : Callback {
                        override fun onSuccess() {
                            binding.mIvPic.visibility=View.VISIBLE
                            binding.shimmerViewContainer.stopShimmer()
                            binding.shimmerViewContainer.visibility=View.GONE
                        }
                        override fun onError(e: Exception?) {
                        }
                    })
            }
            binding.mIvPic.setOnClickListener { listener.onSingleListClick("Click", position) }
            binding.tvDocument.setOnClickListener { listener.onSingleListClick("Click", position) }
            binding.ivMenu.setOnClickListener { listener.onSingleListClick(binding.ivMenu, position) }
            binding.ivMenuTwo.setOnClickListener { listener.onSingleListClick(binding.ivMenuTwo, position) }

            binding.mIvPic.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                longClick.setOnClickListener {
                    listener.onSingleListClick("Click", position)
                }
                return@setOnLongClickListener true
            }
        }
    }
}