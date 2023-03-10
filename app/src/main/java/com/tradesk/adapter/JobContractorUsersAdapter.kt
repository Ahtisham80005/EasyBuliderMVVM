package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallRound
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.Sale
import com.tradesk.R
import com.tradesk.databinding.RowItemContractorusersBinding
import com.tradesk.databinding.RowItemHomeLeadsnewupdateBinding

class JobContractorUsersAdapter (context: Context,
                                 val listener: SingleItemCLickListener,
                                 var mTasksDataFiltered: MutableList<Sale>
) : RecyclerView.Adapter<JobContractorUsersAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemContractorusersBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemContractorusersBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_contractorusers, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            binding.mIvName.text = mTasksDataFiltered[position].name
            if (mTasksDataFiltered[position].image.isNotEmpty()) {
                binding.mIvImage.loadWallRound(mTasksDataFiltered[position].image)
            }
            binding.clParent.setOnClickListener { listener.onSingleItemClick("123", position) }
        }
    }
}