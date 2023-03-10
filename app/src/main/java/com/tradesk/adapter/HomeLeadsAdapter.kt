package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.LeadsData
import com.tradesk.R
import com.tradesk.databinding.RowItemHomeLeadsnewupdateBinding
import com.tradesk.fragment.HomeFragment
import java.util.*

class HomeLeadsAdapter (
    context: Context,
    var homeFragment: HomeFragment,
    val listener: SingleItemCLickListener,
    val longClickListener: LongClickListener,
    var mTasksDataFiltered: MutableList<LeadsData>,
    var mTasksDataOriginal: MutableList<LeadsData>
) : RecyclerView.Adapter<HomeLeadsAdapter.MyViewHolder>(), Filterable {
    class MyViewHolder (var binding: RowItemHomeLeadsnewupdateBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemHomeLeadsnewupdateBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_home_leadsnewupdate, parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            if (mTasksDataFiltered.get(position).type.equals("job")) {
                binding.mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                binding.mTvDate.text = "Sale"
            } else {

                if (mTasksDataFiltered.get(position).status.equals("pending")) {
                    binding.mTvDate.text = "Pending"
                    binding.mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
                } else if (mTasksDataFiltered.get(position).status.equals("follow_up")) {
                    binding.mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
                    binding.mTvDate.text = "Follow Up"
                } else if (mTasksDataFiltered.get(position).status.equals("sale")) {
                    binding.mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                    binding.mTvDate.text = "Sale"
                }
            }
            if (mTasksDataFiltered[position].client.isNotEmpty()) {
                binding.mTvName.text = mTasksDataFiltered[position].client[0].name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }

                binding.mTvPhone.text = insertString(mTasksDataFiltered[position].client[0].phone_no, "", 0)
                binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
                binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
                binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
                binding.mTvPhone.text = "+1 (" + binding.mTvPhone.text.toString()
            } else {
                binding.mTvName.text = "N/A"
                binding.mTvPhone.text = "N/A"
            }

            binding.mTvTitle.text = mTasksDataFiltered[position].project_name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }

            binding.mTvAddress.text = mTasksDataFiltered[position].address.street
//            if(!mTasksDataFiltered[position].address.zipcode.equals("unknown") && !mTasksDataFiltered[position].address.zipcode.equals("000000"))
//            {
//                mTvAddress.text=mTasksDataFiltered[position].address.street+" "+mTasksDataFiltered[position].address.zipcode
//            }

//                        mTasksDataFiltered[position].address.city+ ", " +
//                        mTasksDataFiltered[position].address.zipcode

//            tvMarkComplete.text=if (mTasksDataFiltered[position].task_status.equals("Task Completed")) mTasksDataFiltered[position].task_status else "Mark As Complete"
////            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context,if(mTasksDataFiltered[position].task_status.equals("Mark As Complete")) R.color.blue_color else if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.pink_color))
//            tvMarkComplete.setTextColor(ContextCompat.getColor(tvMarkComplete.context, if(mTasksDataFiltered[position].task_status.equals("Task Completed")) R.color.green_color else  R.color.blue_color))
//
//            tvMarkComplete.setOnClickListener {
//                if(mTasksDataFiltered[position].task_status.equals("Task Completed").not()){
//                    listener.onSingleListClick(mTasksDataFiltered[position],position)
//                }
            binding.parent.setOnClickListener { listener.onSingleItemClick("1", position) }
            binding.parent.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }
            binding.mIvNavigate.setOnClickListener {
                listener.onSingleItemClick(
                    "2",
                    position
                )
            } //navigate click
            binding.mIvEmail.setOnClickListener { listener.onSingleItemClick("3", position) } //email click
            binding.mIvCall.setOnClickListener { listener.onSingleItemClick("4", position) }  //phone click

        }
    }
    override fun getItemCount(): Int = mTasksDataFiltered.size
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
                        if(it.client==null || it.client.isEmpty() )
                        {
                            if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                    Locale.ENGLISH))
//                            ||it.address.city.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                            )
                            {
                                listing.add(it)
                            }
                        }
                        else
                        {
                            if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                    Locale.ENGLISH))
//                            ||it.address.city.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                                || it.client[0].name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                    Locale.ENGLISH))
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
                homeFragment.mList=p1!!.values as MutableList<LeadsData>
                mTasksDataFiltered= p1!!.values as MutableList<LeadsData>
                notifyDataSetChanged()
            }
        }
    }

    open fun insertString(
        originalString: String,
        stringToBeInserted: String?,
        index: Int
    ): String? {
        // Create a new string
        var newString: String? = String()
        for (i in 0 until originalString.length) {
            // Insert the original string character
            // into the new string
            newString += originalString[i]
            if (i == index) {
                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted
            }
        }
        // return the modified String
        return newString
    }
}