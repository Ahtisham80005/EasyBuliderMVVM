package com.tradesk.activity.documentModule

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.AdditionalImageLeadDetail
import com.tradesk.R
import com.tradesk.databinding.RowAllDocumentsBinding

class PermitsAdapter(
    context: Context,
    val listener: SingleListCLickListener,
    var mImages: ArrayList<AdditionalImageLeadDetail>,
    var customCheckBoxListener: CustomCheckBoxListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
    var checkboxVisibility:Boolean,
    var allCheckBoxSelect:Boolean
) : RecyclerView.Adapter<PermitsAdapter.MyViewHolder>() {
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

    override fun getItemCount(): Int = mImages.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
            binding.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    customCheckBoxListener.onCheckBoxClick(position)
                } else {
                    unselectCheckBoxListener.onCheckBoxUnCheckClick(0, position)

                }
            }
            binding.mTitle.text = mImages[position].image.substringAfterLast("/")
            binding.mIvPic.setOnLongClickListener { longClick->
//                longClickListener.onLongClickListenerJobDocs(longClick,position)
                return@setOnLongClickListener true
            }

            binding.mTitle.setOnLongClickListener { longClick->
//                longClickListener.onLongClickListenerJobDocs(longClick,position)
                return@setOnLongClickListener true
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
//                        longClickListener.onLongClickListenerJobDocs(v,position)
                    }
                    return false
                }
            })
            Log.e("Image Link",mImages[position].image)
            binding.myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+mImages[position].image)
            binding.myWebView.setOnLongClickListener{longClick->
//                longClickListener.onLongClickListenerJobDocs(longClick,position)
                return@setOnLongClickListener true}
        }
    }
}