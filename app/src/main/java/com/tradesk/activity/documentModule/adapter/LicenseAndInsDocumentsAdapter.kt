package com.tradesk.activity.documentModule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.Users
import com.tradesk.R
import com.tradesk.databinding.RowAllDocumentsBinding
import java.util.*


class LicenseAndInsDocumentsAdapter(
    context: Context,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<Users>
) : RecyclerView.Adapter<LicenseAndInsDocumentsAdapter.MyViewHolder>(), Filterable {

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
    override fun getItemCount(): Int =  mTasksDataFiltered.size
    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind()
        holder.apply {
            for (i in 0 until mTasksDataFiltered[position].license_and_ins.docs_url.size)
            {
//                  if (mTasksDataFiltered[position].additional_images[i].status == "permit"){
//                    //  mIvPic.loadWallRound(mTasksDataFiltered[position].additional_images[i].image)
//                      break
//                  }
            }
            binding.mTitle.text = "License and insurance"
            binding.mIvPic.setOnClickListener { listener.onSingleListClick("LicenseAndInsDocumentsAdapter", position) }
            binding.mIvPic.visibility= View.GONE
            binding.myWebView.visibility= View.VISIBLE
            binding.myWebView.getSettings().setJavaScriptEnabled(true)
            binding.myWebView.setHorizontalScrollBarEnabled(false);
            binding.myWebView.setVerticalScrollBarEnabled(false);
            binding.myWebView.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        return false
                    }
                    if (event.action == MotionEvent.ACTION_UP) {
                        listener.onSingleListClick("LicenseAndInsDocumentsAdapter", position)
                    }
                    return false
                }
            })
            binding.myWebView.getSettings().setDisplayZoomControls(false)
            binding.myWebView.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.myWebView.visibility=View.VISIBLE
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility=View.GONE
                }
            })

            if(!mTasksDataFiltered[position].license_and_ins.docs_url.isEmpty())
            {
                binding.myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+mTasksDataFiltered[position].license_and_ins.docs_url.get(0))
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()){
//                    mClientListFiltered=mTasksData
                    result.count=mTasksDataFiltered.size
                    result.values=mTasksDataFiltered
                    return result
                }else{
                    val listing= mutableListOf<Users>()
                    mTasksDataFiltered.forEach {
                        if (it.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                Locale.ENGLISH))
                        /*|| it.client_details.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))*/){
                            listing.add(it)
                        }
                    }

                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mTasksDataFiltered= p1!!.values as MutableList<Users>
                notifyDataSetChanged()
            }
        }
    }
}