package com.tradesk.activity.proposalModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadUserImage
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.BuildzerApp
import com.tradesk.Interface.AttachedDocListener
import com.tradesk.R
import com.tradesk.databinding.AttachedDocListItemBinding
class AttachedDocsAdapter(
    context: Context, val listener: AttachedDocListener,
    var mTasksDataFiltered: MutableList<String>
) : RecyclerView.Adapter<AttachedDocsAdapter.MyViewHolder>() {
    class MyViewHolder (var binding: AttachedDocListItemBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: AttachedDocListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.attached_doc_list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            var nameValue = mTasksDataFiltered[position].replace(
                "https://tradesk.s3.us-east-2.amazonaws.com/docs/",
                ""
            )
            val someFilepath = mTasksDataFiltered[position]
            val extension = someFilepath.substring(someFilepath.lastIndexOf("."))
            binding.name.text = nameValue
           if (extension.equals(".jpeg",true)||extension.equals(".jpg",true)||extension.equals(".png",true)) binding.image.loadWallImage(mTasksDataFiltered[position])
            else BuildzerApp.appContext.getDrawable(R.drawable.ic_icon_files)
               ?.let { binding.image.loadUserImage(it) }
            binding.parent.setOnClickListener { listener.onDocClick(mTasksDataFiltered[position], position) }
        }
    }

}