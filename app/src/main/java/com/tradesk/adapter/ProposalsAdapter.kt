package com.tradesk.adapter

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
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.CheckModel
import com.tradesk.Model.Proposal
import com.tradesk.R
import com.tradesk.databinding.AddClientListItemBinding
import com.tradesk.databinding.RowItemProposalsBinding
import java.text.DecimalFormat
import java.util.*

class ProposalsAdapter(context: Context,
                       var mTasksDataOriginal: MutableList<Proposal>,
                       var mTasksDataFiltered: MutableList<Proposal>, val listener: SingleListCLickListener,
                       val longClickListener: LongClickListener,
                       var customCheckBoxListener: CustomCheckBoxListener,
                       val unselectCheckBoxListener: UnselectCheckBoxListener,
                       var checkboxVisibility:Boolean,
                       var allCheckBoxSelect:Boolean,
                       var mcheckBoxModelList: MutableList<CheckModel>
) : RecyclerView.Adapter<ProposalsAdapter.MyViewHolder>(), Filterable {

    class MyViewHolder (var binding: RowItemProposalsBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemProposalsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_proposals, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {

            if(checkboxVisibility)
            {
                binding.mCheckBox.visibility= View.VISIBLE
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

            if (mTasksDataFiltered[position].client_id != null)
            {
                binding.mTvClientName.text = "Client : " + mTasksDataFiltered[position].client_id.name
            }
            else{
                binding.mTvClientName.text = "N/A"
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

            binding.maincard.setOnClickListener {
                listener.onSingleListClick("Pdf", position)
            }

            binding.maincard.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
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
                    result.count = mTasksDataOriginal.size
                    result.values = mTasksDataOriginal
                    return result
                }
                else {
                    val listing = mutableListOf<Proposal>()
                    mTasksDataOriginal.forEach {
                        if(it.client_id!=null)
                        {
                            if (it.client_id.name.trim().lowercase(Locale.ENGLISH).contains(p0.toString().trim().lowercase(Locale.ENGLISH))
                            /*|| it.client_details.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))*/) {
                                listing.add(it)
                            }
                        }
                    }
                    result.count = listing.size
                    result.values = listing
                    return result
                }
            }
            override fun publishResults(p0: CharSequence?, result: FilterResults?) {
                mTasksDataFiltered = result!!.values as MutableList<Proposal>
                notifyDataSetChanged()
            }
        }
    }
}