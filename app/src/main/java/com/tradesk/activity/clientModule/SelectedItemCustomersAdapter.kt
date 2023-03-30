package com.tradesk.activity.clientModule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Client
import com.tradesk.R
import com.tradesk.databinding.RowItemNoteslistBinding
import com.tradesk.databinding.RowSelectedCustomerListBinding
import com.tradesk.util.Constants.insertString
import java.util.*

class SelectedItemCustomersAdapter(
    context: Context, var selectType:String,
    var mClientsList: MutableList<Client>,
    var mClientsListOriginal: MutableList<Client>,
    val listener: SingleListCLickListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
) : RecyclerView.Adapter<SelectedItemCustomersAdapter.MyViewHolder>(), Filterable {

    var incr = 0
    var decr = 0
    class MyViewHolder (var binding: RowSelectedCustomerListBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowSelectedCustomerListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_selected_customer_list, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mClientsList.size

    override fun onBindViewHolder(holder:MyViewHolder, position: Int) {
        holder.apply {

            binding.mTvName.text = mClientsList[position].name
            binding.mTvEmail.text = mClientsList[position].email
            binding.mTvPhone.text = insertString(mClientsList[position].phone_no, "", 0)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
            binding.mTvPhone.text = "(" + binding.mTvPhone.text.toString()
            binding.mTvAddress.text =
                mClientsList[position].address.city + ", " + mClientsList[position].address.state
            binding.mCheckBok.isChecked = false
            binding.mCheckBok.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    incr++
                    listener.onSingleListClick(incr,position)

                }else{
                    decr++
                    unselectCheckBoxListener.onCheckBoxUnCheckClick(decr,position)
                }
            }

            if (selectType.equals("all")){
                binding.mCheckBok.isChecked = true
                binding.mCheckBok.isClickable = false
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