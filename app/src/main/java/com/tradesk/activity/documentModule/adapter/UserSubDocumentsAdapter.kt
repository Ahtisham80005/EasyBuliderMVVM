package com.tradesk.activity.documentModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.CheckModel
import com.tradesk.R
import com.tradesk.databinding.RowAllDocumentsBinding

class UserSubDocumentsAdapter(
    context: Context,
    val listener: SingleListCLickListener,
    var mList: MutableList<String>,
    val longClickListener: LongClickListener,
    var customCheckBoxListener: CustomCheckBoxListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
    var checkboxVisibility:Boolean,
    var allCheckBoxSelect:Boolean,
    var mcheckBoxModelList: MutableList<CheckModel>
) : RecyclerView.Adapter<UserSubDocumentsAdapter.MyViewHolder>() {

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

    override fun getItemCount(): Int = mList.size

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

            for (i in 0 until mList.size) {
                binding.mTitle.text = mList[position].substringAfterLast("/")
//                Log.d(TAG, "onBindViewHolder: " + mList[position])
                break
            }
            binding.mIvPic.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }

            itemView.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }
            binding.mIvPic.visibility= View.GONE
            binding.myWebView.visibility= View.VISIBLE
            binding.myWebView.getSettings().setJavaScriptEnabled(true)
            binding.myWebView.setHorizontalScrollBarEnabled(false)
            binding.myWebView.setVerticalScrollBarEnabled(false)
            binding.myWebView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+mList[position])
            binding.myWebView.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        return false
                    }
                    if (event.action == MotionEvent.ACTION_UP) {
                        listener.onSingleListClick("UserGalleryAdapter", position)
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
            binding.myWebView.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }
        }
    }
}