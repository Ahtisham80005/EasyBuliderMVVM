package com.tradesk.activity.galleryModule.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.UsersAdditionalImageX
import com.tradesk.R
import com.tradesk.activity.galleryModule.GallaryActivity
import com.tradesk.databinding.RowItemAdditionalGalleryUpdatedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class UserGallaryAdapter(
    var activity: GallaryActivity,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<UsersAdditionalImageX>,
    var mTasksDataOrginal: MutableList<UsersAdditionalImageX>
) : RecyclerView.Adapter<UserGallaryAdapter.MyViewHolder>(), Filterable {

    class MyViewHolder (var binding: RowItemAdditionalGalleryUpdatedBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemAdditionalGalleryUpdatedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_additional_gallery_updated, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =  mTasksDataFiltered.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {

            if(mTasksDataFiltered[position].folder_name != null) {
                val title = mTasksDataFiltered[position].folder_name.capitalize()
                binding.mTitle.text = title.toString()
            }
            for (i in 0 until mTasksDataFiltered[position].images.size) {
                // if (mTasksDataFiltered[position].additional_images[i].status == "image"){
//                      mIvPic.loadWallRound(mTasksDataFiltered[position].images[i])

                CoroutineScope(Dispatchers.Main).launch {
                    Picasso.get()
                        .load(mTasksDataFiltered[position].images[i])
                        .resize(200, 200)
                        .centerCrop()
                        .into(binding.mIvPic, object : Callback {
                            override fun onSuccess() {
                                binding.mIvPic.visibility = View.VISIBLE
                                binding.shimmerViewContainer.stopShimmer()
                                binding.shimmerViewContainer.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {

                            }
                        })
                }
                break
            }

            binding.mIvPic.setOnClickListener { listener.onSingleListClick("UserGalleryAdapter", position) }
            binding.mTitle.setOnClickListener { listener.onSingleListClick("UserGalleryAdapter", position) }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()){
                    result.count=mTasksDataOrginal.size
                    result.values=mTasksDataOrginal
                    return result
                }else{
                    val listing= mutableListOf<UsersAdditionalImageX>()
                    mTasksDataOrginal.forEach {
                        if (it.folder_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                Locale.ENGLISH))
                        /*|| it.client_details.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))*/){
                            listing.add(it)
                        }
                    }
                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mTasksDataFiltered= p1!!.values as MutableList<UsersAdditionalImageX>
                activity.mListUserGallery= mTasksDataFiltered as ArrayList<UsersAdditionalImageX>
                notifyDataSetChanged()
            }
        }
    }
}