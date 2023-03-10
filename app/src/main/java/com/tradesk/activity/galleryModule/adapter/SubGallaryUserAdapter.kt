package com.tradesk.activity.galleryModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.R
import com.tradesk.databinding.RowItemSubAdditionalGalleryUpdatedBinding
import com.tradesk.databinding.RowSubGalleryUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubGallaryUserAdapter (context: Context,
                             val listener: SingleListCLickListener,
                             var mList: MutableList<String>,
                             val longClickListener: LongClickListener,
                             var customCheckBoxListener: CustomCheckBoxListener,
                             val unselectCheckBoxListener: UnselectCheckBoxListener,
                             var checkboxVisibility:Boolean,
                             var allCheckBoxSelect:Boolean)
    : RecyclerView.Adapter<SubGallaryUserAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowSubGalleryUserBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowSubGalleryUserBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_sub_gallery_user, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =    mList.size
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

//            for (i in 0 until mList.size) {
//                mIvPic.loadWallRound(mList[position])
            CoroutineScope(Dispatchers.Main).launch {
                Picasso.get()
                    .load(mList[position])
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
            binding.mIvPic.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
//                longClick.setOnClickListener {
//                    listener.onSingleListClick("Multi", position)
//                }
                return@setOnLongClickListener true
            }

            binding.mIvPic.setOnClickListener { listener.onSingleListClick(binding.mIvPic, position) }

            binding.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    customCheckBoxListener.onCheckBoxClick(position)
                } else {
                    unselectCheckBoxListener.onCheckBoxUnCheckClick(0, position)

                }
            }
        }
    }

}