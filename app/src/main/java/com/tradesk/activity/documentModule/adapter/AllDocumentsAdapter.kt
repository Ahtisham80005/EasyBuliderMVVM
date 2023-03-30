package com.tradesk.activity.documentModule.adapter

import android.annotation.SuppressLint
import android.util.Log
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
import com.tradesk.Model.JobDocuments
import com.tradesk.R
import com.tradesk.activity.documentModule.DocumentsActivity
import com.tradesk.databinding.RowAllDocumentsBinding
import java.util.*

class AllDocumentsAdapter(
    var activity: DocumentsActivity,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<JobDocuments>,
    var mTasksDataOrginal: MutableList<JobDocuments>
) : RecyclerView.Adapter<AllDocumentsAdapter.MyViewHolder>(), Filterable {

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
            binding.mTitle.text = mTasksDataFiltered[position].project_name
            if(!mTasksDataFiltered[position].users_assigned.isEmpty())
            {
                for (i in 0 until mTasksDataFiltered[position].users_assigned.size)
                {
                    if (mTasksDataFiltered[position].users_assigned[i].user_id != null) {
                        binding.mTitle.text = mTasksDataFiltered[position].project_name.capitalize() + " - " + mTasksDataFiltered[position].users_assigned[i].user_id.name.toString()
                        break
                    }
                }
            }
            binding.mTitle.setOnClickListener {
                listener.onSingleListClick("Multi", position)
            }

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
                        listener.onSingleListClick("Multi", position)
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
            for(aDocuments in mTasksDataFiltered[position].additional_images)
            {
                if(aDocuments.status.equals("permit"))
                {
                    Log.e("Image Link",aDocuments.image+" My id ="+aDocuments._id)
                    binding.myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+aDocuments.image)
                    break
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()){
//                    mClientListFiltered=mTasksData
                    result.count=mTasksDataOrginal.size
                    result.values=mTasksDataOrginal
                    return result
                }else{
                    val listing= mutableListOf<JobDocuments>()
                    mTasksDataOrginal.forEach {
                        if(it.users_assigned==null || it.users_assigned.isEmpty() || it.users_assigned[0].user_id==null)
                        {
                            if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                    Locale.ENGLISH))
//                             || it.users_assigned[0].user_id.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
                            ){
                                listing.add(it)
                            }
                        }
                        else{
                            it.users_assigned.forEach {useraray->
                                if (useraray.user_id != null && useraray.user_id.name != null) {
                                    if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                            Locale.ENGLISH))
                                        || useraray.user_id.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                            Locale.ENGLISH))
                                    ){
                                        listing.add(it)
                                    }
                                }
                            }
//                            if (it.project_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
//                                || it.users_assigned[0].user_id.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(Locale.ENGLISH))
//                            ){
//                                listing.add(it)
//                            }
                        }
                    }
                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mTasksDataFiltered= p1!!.values as MutableList<JobDocuments>
                activity.mListAllDocuments=mTasksDataFiltered
                notifyDataSetChanged()
            }
        }
    }
}