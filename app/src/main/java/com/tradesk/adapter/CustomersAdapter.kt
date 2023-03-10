package com.tradesk.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.BuildzerApp
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.Client
import com.tradesk.R
import com.tradesk.databinding.AddClientListItemBinding
import com.tradesk.databinding.RowItemCustomerslistBinding
import com.tradesk.util.Constants.insertString
import java.util.*

class CustomersAdapter (
    context: Context,
    var mClientsList: MutableList<Client>,
    var mClientsListOriginal: MutableList<Client>,
    val listener: SingleListCLickListener,
    val longClickListener: LongClickListener
) : RecyclerView.Adapter<CustomersAdapter.MyViewHolder>(), Filterable {

    class MyViewHolder (var binding: RowItemCustomerslistBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemCustomerslistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_customerslist, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =  mClientsList.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {

            binding.mTvName.text = mClientsList[position].name
            binding.mTvEmail.text = mClientsList[position].email
            binding.mTvPhone.text = insertString(mClientsList[position].phone_no, "", 0)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
            binding.mTvPhone.text = "(" + binding.mTvPhone.text.toString()
            binding.mTvAddress.text = mClientsList[position].address.street

            binding.clEdit.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("smsto:" + "+1 " + mClientsList[position].phone_no)
                intent.putExtra("sms_body", "")
                BuildzerApp.appContext.startActivity(intent)
            }
            binding.textView7.setOnClickListener {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                dialIntent.data = Uri.parse("tel:" + "+1 " + mClientsList[position].phone_no)
                BuildzerApp.appContext. startActivity(dialIntent)
            }
            binding.clMain.setOnClickListener {
                listener.onSingleListClick("main", position)
            }

            binding.clMain.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            var result = FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()) {
//                    mTasksDataFiltered=mTasksData
                    result.count = mClientsListOriginal.size
                    result.values = mClientsListOriginal
                    return result
                } else {
                    val listing = mutableListOf<Client>()
                    mClientsListOriginal.forEach {
                        if (it.name.lowercase(Locale.ENGLISH)
                                .contains(p0.toString().lowercase(Locale.ENGLISH))
                        ) {
                            listing.add(it)
                        }
                    }

                    result.count = listing.size
                    result.values = listing
                    return result
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mClientsList = p1!!.values as MutableList<Client>
                notifyDataSetChanged()
            }
        }
    }
}