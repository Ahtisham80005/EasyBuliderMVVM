package com.tradesk.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.BuildzerApp
import com.tradesk.Interface.DocListener
import com.tradesk.R
import com.tradesk.databinding.DocsItemListBinding
import com.tradesk.databinding.RowItemCustomerslistBinding

class DocsAdapter(
    var context: Context,
    val listener: DocListener,
    var mTasksDataFiltered: MutableList<String>
) : RecyclerView.Adapter<DocsAdapter.MyViewHolder>()  {
    class MyViewHolder (var binding: DocsItemListBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: DocsItemListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.docs_item_list, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder:MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            var nameValue = mTasksDataFiltered[position].replace(
                "https://tradesk.s3.us-east-2.amazonaws.com/docs/",
                ""
            )
            val someFilepath = mTasksDataFiltered[position]
            binding.docName.text = nameValue
            binding.parent.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(someFilepath))
                browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                BuildzerApp.appContext.startActivity(browserIntent)
            }
            binding.crossDoc.setOnClickListener {
                listener.onCrossClick(mTasksDataFiltered[position],position)
            }

        }
    }

}