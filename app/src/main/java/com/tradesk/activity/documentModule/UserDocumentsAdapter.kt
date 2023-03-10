package com.tradesk.activity.documentModule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.AdditionalDocument
import com.tradesk.R
import com.tradesk.databinding.AddClientListItemBinding
import com.tradesk.databinding.RowAllDocumentsBinding
import java.util.*

class UserDocumentsAdapter (
    var activity: DocumentsActivity,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<AdditionalDocument>,
    var mTasksDataOrginal: MutableList<AdditionalDocument>
) : RecyclerView.Adapter<UserDocumentsAdapter.MyViewHolder>(), Filterable {

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
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {

            var title = if(mTasksDataFiltered[position].folder_name != null) {
                mTasksDataFiltered[position].folder_name.capitalize()
            } else {
                mTasksDataFiltered[position].folder_name.capitalize()
            }
            binding.mTitle.text = title
            binding.mTitle.setOnClickListener {
                listener.onSingleListClick("UserDocumentsAdapter", position)
            }

            for (i in 0 until mTasksDataFiltered[position].documents.size) {
//                  if (mTasksDataFiltered[position].additional_images[i].status == "permit"){
//                    //  mIvPic.loadWallRound(mTasksDataFiltered[position].additional_images[i].image)
//                      break
//                  }
            }

            binding.mIvPic.setOnClickListener { listener.onSingleListClick("UserDocumentsAdapter", position) }
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
                        listener.onSingleListClick("UserDocumentsAdapter", position)
                    }
                    return false
                }
            })
            if(!mTasksDataFiltered[position].documents.isEmpty())
            {
                binding.myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+mTasksDataFiltered[position].documents.get(0))
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
                    val listing= mutableListOf<AdditionalDocument>()
                    mTasksDataOrginal.forEach {
                        if (it.folder_name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
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
                activity.mUserAdditionalDocumentsList=p1!!.values as MutableList<AdditionalDocument>
                mTasksDataFiltered= p1!!.values as MutableList<AdditionalDocument>
                notifyDataSetChanged()
            }
        }
    }

}