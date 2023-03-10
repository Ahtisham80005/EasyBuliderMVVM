package com.tradesk.activity.galleryModule.adapter

import android.app.Activity
import android.util.Log
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
import com.tradesk.Model.LeadsDataImageClient
import com.tradesk.R
import com.tradesk.activity.galleryModule.GallaryActivity
import com.tradesk.databinding.RowItemAdditionalGalleryUpdatedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class AdditionalGallaryAdapter(val activity: Activity,
                               val listener: SingleListCLickListener,
                               var mTasksDataFiltered: MutableList<LeadsDataImageClient>,
                               var mTasksDataOriginal: MutableList<LeadsDataImageClient>
) : RecyclerView.Adapter<AdditionalGallaryAdapter.MyViewHolder>(), Filterable {

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
    override fun getItemCount(): Int =   mTasksDataFiltered.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {

            binding.mTitle.text = mTasksDataFiltered[position].project_name

            if(!mTasksDataFiltered[position].users_assigned.isEmpty())
            {
                for (i in 0 until mTasksDataFiltered[position].users_assigned.size)
                {
                    if (mTasksDataFiltered[position].users_assigned[i].user_id != null)
                    {
                        binding.mTitle.text = mTasksDataFiltered[position].project_name + " - " + mTasksDataFiltered[position].users_assigned[i].user_id.name
                        break
                    }
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                for (i in 0 until mTasksDataFiltered[position].additional_images.size) {
                    if (mTasksDataFiltered[position].additional_images[i].status == "image"){
                        Picasso.get()
                            .load(mTasksDataFiltered[position].additional_images[i].image)
                            .resize(200,200)
                            .centerCrop()
                            .into(binding.mIvPic, object : Callback {
                                override fun onSuccess() {
                                    binding.mIvPic.visibility= View.VISIBLE
                                    binding.shimmerViewContainer.stopShimmer()
                                    binding.shimmerViewContainer.visibility= View.GONE
                                }
                                override fun onError(e: Exception?) {

                                }
                            })
                        break
                    }
                }
            }
            binding.mIvPic.setOnClickListener { listener.onSingleListClick("Multi", position) }
            binding.mTitle.setOnClickListener { listener.onSingleListClick("Multi", position) }
            binding.ivMenu.setOnClickListener { listener.onSingleListClick(binding.ivMenu, position) }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter()
        {
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults
            {
//                Toast.makeText(context,"Adapter "+p0,Toast.LENGTH_SHORT).show()
                if (p0!!.isEmpty())
                {
//                    Toast.makeText(context,"Empty "+p0,Toast.LENGTH_SHORT).show()
//                    mClientListFiltered=mTasksData
                    result.count=mTasksDataOriginal.size
                    result.values=mTasksDataOriginal
                    return result
                }
                else {
//                    Toast.makeText(context, "Not Empty " + p0, Toast.LENGTH_SHORT).show()
                    val listing = mutableListOf<LeadsDataImageClient>()
                    mTasksDataOriginal.forEach {

                        if(it.users_assigned==null || it.users_assigned.isEmpty() || it.users_assigned[0].user_id==null)
                        {
                            if (it.project_name.lowercase(Locale.ENGLISH).contains(p0.toString().lowercase(
                                    Locale.ENGLISH)))
                            {
                                listing.add(it)
                            }
                        }
                        else
                        {
                            if (it.project_name.lowercase(Locale.ENGLISH).contains(p0.toString().lowercase(
                                    Locale.ENGLISH))
                                || it.users_assigned[0].user_id.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                    Locale.ENGLISH))
                            )
                            {
                                listing.add(it)
                            }
                        }
                    }
//                    Log.e("upper List Size",listing.size.toString())
                    result.count = listing.size
                    result.values = listing
                    return result
                }
            }
            override fun publishResults(p0: CharSequence?, result: FilterResults?) {
                mTasksDataFiltered=result!!.values as MutableList<LeadsDataImageClient>
                if(activity is GallaryActivity)
                {
                    activity.mListUpdatedAdditionalImage= mTasksDataFiltered as ArrayList<LeadsDataImageClient>
                }
//                else if(activity is SubGallaryActivity)
//                {
//                    activity.mListUpdatedAdditionalImage= mTasksDataFiltered as ArrayList<LeadsDataImageClient>
//                }
                notifyDataSetChanged()
                Log.e("Current List Size",mTasksDataFiltered.size.toString())
            }
        }
    }
}