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
import com.tradesk.Model.AdditionalImageImageClient
import com.tradesk.R
import com.tradesk.databinding.RowItemAdditionalGalleryUpdatedBinding
import com.tradesk.databinding.RowItemGalleryBinding
import com.tradesk.databinding.RowItemSubAdditionalGalleryUpdatedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class SubAdditionalGallaryAdapter(
    var mTasksDataFiltered: MutableList<AdditionalImageImageClient>,
    val listener: SingleListCLickListener,
    val longClickListener: LongClickListener,
    var customCheckBoxListener: CustomCheckBoxListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
    var checkboxVisibility:Boolean,
    var allCheckBoxSelect:Boolean
) : RecyclerView.Adapter<SubAdditionalGallaryAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemSubAdditionalGalleryUpdatedBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemSubAdditionalGalleryUpdatedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_sub_additional_gallery_updated, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =   mTasksDataFiltered.size
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
            binding.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    customCheckBoxListener.onCheckBoxClick(position)
                } else {
                    unselectCheckBoxListener.onCheckBoxUnCheckClick(0, position)
                }
            }
            for (i in 0 until mTasksDataFiltered.size) {
                if (mTasksDataFiltered[position].status.equals("image")){

                    CoroutineScope(Dispatchers.Main).launch {
                        Picasso.get()
                            .load(mTasksDataFiltered[position].image)
//                        .placeholder(R.drawable.ic_user)
//                        .fit()
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
                    break
                }
            }
            binding.mTitle.visibility=View.GONE
            binding.mIvPic.setOnClickListener { listener.onSingleListClick("Multi", position) }
            binding.ivMenu.setOnClickListener { listener.onSingleListClick(binding.ivMenu, position) }

            binding.mIvPic.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                longClick.setOnClickListener {
                    listener.onSingleListClick("Multi", position)
                }
                return@setOnLongClickListener true
            }


        }
    }


}