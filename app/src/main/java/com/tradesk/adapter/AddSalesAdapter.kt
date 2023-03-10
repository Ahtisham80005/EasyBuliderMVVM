package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.AddSalesListener
import com.tradesk.Model.Client
import com.tradesk.R
import com.tradesk.databinding.AddClientListItemBinding

class AddSalesAdapter (context: Context,
                       var mClientListFiltered: MutableList<Client>,
                       var mClientListOriginal: MutableList<Client>,
                       var listenerAdd: AddSalesListener
) : RecyclerView.Adapter<AddSalesAdapter.MyViewHolder>() {
    class MyViewHolder (var binding: AddClientListItemBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: AddClientListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.add_client_list_item, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =  mClientListFiltered.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            if (mClientListFiltered[position].image.isNotEmpty()) {
                binding.circleImageView.loadWallImage(mClientListFiltered[position].image)
            }
            binding.name.setText(mClientListFiltered[position].name)
            binding.cross.setOnClickListener {
                listenerAdd.onAddSalesClick(mClientListFiltered[position],position)
            }
        }
    }
}