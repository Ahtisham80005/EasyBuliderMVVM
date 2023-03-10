package com.tradesk.activity.proposalModule.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.OnItemRemove
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.AddItemDataUpdateProposal
import com.tradesk.R
import com.tradesk.databinding.RowItemPorposalitemsBinding

class ProSelectedItemsAdapter (
    context: Context,
    val listener: SingleItemCLickListener,
    val removelistener: OnItemRemove,
    var mItems: MutableList<AddItemDataUpdateProposal>) : RecyclerView.Adapter<ProSelectedItemsAdapter.MyViewHolder>(){

    class MyViewHolder (var binding: RowItemPorposalitemsBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun getItemCount(): Int = mItems.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemPorposalitemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_porposalitems, parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            binding.tvName.setText(mItems[position].title)
            binding.tvPrice.setText("$"+mItems[position].amount)
            binding.quantity.setText(""+mItems[position].quantity)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvDesc.setText(Html.fromHtml(mItems[position].description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                binding.tvDesc.setText(Html.fromHtml(mItems[position].description));
            }
            binding.cross.setOnClickListener { removelistener.onRemove("cancel",position)  }
        }
    }
}