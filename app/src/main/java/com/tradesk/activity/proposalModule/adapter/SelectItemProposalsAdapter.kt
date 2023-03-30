package com.tradesk.activity.proposalModule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.BuildzerApp
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Proposal
import com.tradesk.R
import com.tradesk.databinding.RowItemPorposalitemsBinding
import com.tradesk.databinding.RowItemSelectItemProposalsBinding
import java.text.DecimalFormat
import java.util.*

class SelectItemProposalsAdapter (
    context: Context, var selectType:String,
    var mTasksDataOriginal: MutableList<Proposal>,
    var mTasksDataFiltered: MutableList<Proposal>,
    val listener: SingleListCLickListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
) : RecyclerView.Adapter<SelectItemProposalsAdapter.MyViewHolder>(), Filterable {

    var TAG = "SelectItemPropsalActivity"
    var visiblePos = -1
    var incr = 0
    var decr = 0

    class MyViewHolder (var binding: RowItemSelectItemProposalsBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemSelectItemProposalsBinding= DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_select_item_proposals, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int = mTasksDataFiltered.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            if (mTasksDataFiltered[position].client_id != null) {
                binding.mTvClientName.text = "Client : " + mTasksDataFiltered[position].client_id.name
            }
            val formatter = DecimalFormat("#,###,###")
            binding.mTvTotal.setText(
                "$ " + formatter.format(
                    mTasksDataFiltered[position].total.replace(
                        ",",
                        ""
                    ).toInt()
                )
            )
            try {
                binding.mTvInfo.text = mTasksDataFiltered[position].items[0].description
            } catch (e: Exception) {
                Toast.makeText(BuildzerApp.appContext, e.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
            if (mTasksDataFiltered[position].status.equals("Inprocess")) {
//                mtvStatus.setText("Ongoing")
                binding.mTvDate.setBackgroundResource(R.drawable.round_shape_coloredleads)
                binding.mTvDate.setText("Ongoing")
            } else {
                if (mTasksDataFiltered.get(position).status.equals("pending")) {
                    binding.mTvDate.setText("Pending")
                    binding.mTvDate.setBackgroundResource(R.drawable.round_shape_reload)
                } else if (mTasksDataFiltered.get(position).status.equals("completed")) {
                    binding.mTvDate.setBackgroundResource(R.drawable.round_shape_sale)
                    binding.mTvDate.setText("Complete")
                }
            }
            if (mTasksDataFiltered[position].type.equals("Invoice",true)) {
                binding.editIcon.visibility= View.GONE
            }
            Log.d(TAG, "onBindViewHolder: ")

            binding.mCheckBok.isChecked = false

            binding.mCheckBok.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    incr++
                    listener.onSingleListClick(incr,position)

                }else{
                    decr++
                    unselectCheckBoxListener.onCheckBoxUnCheckClick(decr,position)
                }
            }

            if (selectType.equals("all")){
                binding.mCheckBok.isChecked = true
                binding.mCheckBok.isClickable = false
            }

            binding.mIvDelete.setOnClickListener {
                listener.onSingleListClick("Delete", position)
            }
            binding.editIcon.setOnClickListener {
                listener.onSingleListClick("edit", position)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            var result = FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()) {
//                    mClientListFiltered=mTasksData
                    result.count = mTasksDataOriginal.size
                    result.values = mTasksDataOriginal
                    return result
                } else {
                    val listing = mutableListOf<Proposal>()
                    mTasksDataOriginal.forEach {
                        if (it.client_id.name.toLowerCase(Locale.ENGLISH)
                                .contains(p0.toString().toLowerCase(Locale.ENGLISH))
                        /*|| it.client_details.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))*/) {
                            listing.add(it)
                        }
                    }

                    result.count = listing.size
                    result.values = listing
                    return result
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mTasksDataFiltered = p1!!.values as MutableList<Proposal>
                notifyDataSetChanged()
            }
        }
    }
}