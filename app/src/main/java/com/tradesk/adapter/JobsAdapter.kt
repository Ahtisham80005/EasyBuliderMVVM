package com.tradesk.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.LeadsData
import com.tradesk.R
import com.tradesk.databinding.RowItemJobsNewBinding
import com.tradesk.fragment.JobsFragment
import com.tradesk.util.Constants.insertString
import java.util.*

class JobsAdapter(
    activity: Activity,
    var jobFragment: JobsFragment,
    val listener: SingleListCLickListener,
    val longClickListener: LongClickListener,
    var mTasksDataFiltered: MutableList<LeadsData>,
    var mTasksDataOriginal: MutableList<LeadsData>,
) : RecyclerView.Adapter<JobsAdapter.MyViewHolder>() , Filterable {
    class MyViewHolder (var binding: RowItemJobsNewBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemJobsNewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_jobs_new, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =  mTasksDataFiltered.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            if (mTasksDataFiltered.get(position).status.equals("pending"))
            {
                binding.mTvDate.text = "Pending"
                binding.mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
            } else if (mTasksDataFiltered.get(position).status.equals("ongoing")) {
                binding.mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
                binding.mTvDate.text = "Ongoing"
            } else if (mTasksDataFiltered.get(position).status.equals("completed")) {
                binding.mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                binding.mTvDate.text = "Complete"
            } else if (mTasksDataFiltered.get(position).status.equals("cancel")) {
                binding.mTvDate.setBackgroundResource(R.drawable.round_shape_cancel)
                binding.mTvDate.text = "Cancelled"
            }

            if (mTasksDataFiltered[position].client.isNotEmpty()) {
                binding.mTvName.text = mTasksDataFiltered[position].client[0].name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                binding.mTvNumber.text = insertString(mTasksDataFiltered[position].client[0].phone_no, "", 0)
                binding.mTvNumber.text = insertString(binding.mTvNumber.text.toString(), ")", 2)
                binding.mTvNumber.text = insertString(binding.mTvNumber.text.toString(), " ", 3)
                binding.mTvNumber.text = insertString(binding.mTvNumber.text.toString(), "-", 7)
                binding.mTvNumber.text = "+1 (" + binding.mTvNumber.text.toString()
            }
            else {
                binding.mTvName.text = "N/A"
                binding.mTvNumber.text = "N/A"
            }

            binding.mTvTitle.text = mTasksDataFiltered[position].project_name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }

            binding.mTvAddress.text = mTasksDataFiltered[position].address.street

            binding.parent.setOnClickListener { listener.onSingleListClick(binding.parent, position) }

            binding.parent.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }

            binding.mIvNavigate.setOnClickListener {
                listener.onSingleListClick(
                    "2",
                    position
                )
            } //navigate click
            binding.mIvEmail.setOnClickListener { listener.onSingleListClick("3", position) } //email click
            binding.mIvCall.setOnClickListener { listener.onSingleListClick("4", position) }  //phone click
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()){
//                    mClientListFiltered=mTasksData
                    result.count=mTasksDataOriginal.size
                    result.values=mTasksDataOriginal
                    return result
                }else{
                    val listing= mutableListOf<LeadsData>()
                    mTasksDataOriginal.forEach {

                        if(it.client==null || it.client.isEmpty())
                        {
                            if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
//                            ||it.address.city.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                            ){
                                listing.add(it)
                            }
                        }
                        else{
                            if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
//                            ||it.address.city.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                                || it.client[0].name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                            ){
                                listing.add(it)
                            }
                        }
                    }

                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                jobFragment.mList=p1!!.values as MutableList<LeadsData>
                mTasksDataFiltered= p1!!.values as MutableList<LeadsData>
                notifyDataSetChanged()

            }
        }
    }
}