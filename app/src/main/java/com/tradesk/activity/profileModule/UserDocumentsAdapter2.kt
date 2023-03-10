package com.tradesk.activity.profileModule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallRound
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.R
import com.tradesk.databinding.RowAllDocumentsBinding
import com.tradesk.databinding.RowItemDocumentsBinding

class UserDocumentsAdapter2(
    context: Context,
    val listener: SingleItemCLickListener,
    var mDocs: MutableList<String>,
) : RecyclerView.Adapter<UserDocumentsAdapter2.MyViewHolder>()  {

    class MyViewHolder (var binding: RowItemDocumentsBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemDocumentsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_documents, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int = mDocs.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            val ext = MimeTypeMap.getFileExtensionFromUrl(mDocs[position])
            val isDoc = ext.equals("pdf", true) || ext.equals("docx", true) || ext.equals("doc", true)
            binding.mIvDocs.isVisible = (isDoc).not()
            binding.tvDocument.isVisible = isDoc
            if (isDoc) {
                binding.tvDocument.text=mDocs[position].substringAfterLast("/")
            } else {
                binding.mIvDocs.loadWallRound(mDocs[position])
            }
            binding.clParent.setOnClickListener { listener.onSingleItemClick("detail", position) }
        }
    }
}