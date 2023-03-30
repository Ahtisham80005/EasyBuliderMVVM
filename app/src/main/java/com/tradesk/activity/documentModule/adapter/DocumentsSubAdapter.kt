package com.tradesk.activity.documentModule.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.AdditionalImageDocuments
import com.tradesk.Model.CheckModel
import com.tradesk.R
import com.tradesk.databinding.RowAllDocumentsBinding

class DocumentsSubAdapter (
    var myContext: Context,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<AdditionalImageDocuments>,
    val longClickListener: LongClickListener,
    var customCheckBoxListener: CustomCheckBoxListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
    var checkboxVisibility:Boolean,
    var allCheckBoxSelect:Boolean,
    var mcheckBoxModelList: MutableList<CheckModel>
) : RecyclerView.Adapter<DocumentsSubAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowAllDocumentsBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowAllDocumentsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_all_documents, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mTasksDataFiltered.size

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
            binding.mTitle.text = mTasksDataFiltered[position].image.substringAfterLast("/")

            itemView.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }

            binding.mIvPic.visibility= View.GONE
            binding.myWebView.visibility= View.VISIBLE
            binding.myWebView.getSettings().setJavaScriptEnabled(true)
            binding.myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+mTasksDataFiltered[position].image)
            binding.myWebView.setHorizontalScrollBarEnabled(false);
            binding.myWebView.setVerticalScrollBarEnabled(false);
            binding.myWebView.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }
            Log.e("Image Link",mTasksDataFiltered[position].image)
            binding.myWebView.getSettings().setDisplayZoomControls(false)
            binding.myWebView.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.myWebView.visibility=View.VISIBLE
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility=View.GONE
                }
            })
        }
    }
}