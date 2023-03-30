package com.tradesk.activity.proposalModule.adapter

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.ItemsData
import com.tradesk.R
import com.tradesk.databinding.RowItemDefaultlistBinding
import com.tradesk.databinding.RowItemProposalsBinding

class DefaultItemsAdapter(
    context: Context,
    val listener: SingleItemCLickListener,
    var mTasksDataFiltered: MutableList<ItemsData>) : RecyclerView.Adapter<DefaultItemsAdapter.MyViewHolder>() {


    class MyViewHolder (var binding: RowItemDefaultlistBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemDefaultlistBinding= DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_defaultlist, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            itemView.setOnClickListener { listener.onSingleItemClick("1",position)  }
            binding.mTvDesc.setOnClickListener { listener.onSingleItemClick("1",position)  }
            binding.mTvTitle.setOnClickListener { listener.onSingleItemClick("1",position)  }
            binding.mTvDesc.setOnClickListener { listener.onSingleItemClick("1",position)  }
            binding.clParent.setOnClickListener { listener.onSingleItemClick("1",position)  }

            binding.mTvTitle.setText(mTasksDataFiltered[position].title)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.mTvDesc.setText(Html.fromHtml(mTasksDataFiltered[position].description, Html.FROM_HTML_MODE_COMPACT));
            } else {
                binding.mTvDesc.setText(Html.fromHtml(mTasksDataFiltered[position].description));
            }
        }
    }


}