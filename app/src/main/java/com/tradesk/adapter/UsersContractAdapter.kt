package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.CheckModel
import com.tradesk.Model.Client
import com.tradesk.R
import com.tradesk.databinding.RowItemCalendaritemsBinding
import com.tradesk.databinding.RowItemUserscontractBinding
import com.tradesk.util.Constants.insertString
import java.util.*

class UsersContractAdapter (
    context: Context,
    var mClientListFiltered: MutableList<Client>,
    var mClientListOriginal: MutableList<Client>,
    val listener: SingleListCLickListener,
    val longClickListener: LongClickListener,
    var customCheckBoxListener: CustomCheckBoxListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
    var checkboxVisibility:Boolean,
    var allCheckBoxSelect:Boolean,
    var mcheckBoxModelList: MutableList<CheckModel>
) : RecyclerView.Adapter<UsersContractAdapter.MyViewHolder>(),
    Filterable {

    class MyViewHolder (var binding: RowItemUserscontractBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }

//    override fun onViewRecycled(holder: MyViewHolder) {
////        holder.binding.mCheckBox.setChecked(false)
//        super.onViewRecycled(holder)
//    }

    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemUserscontractBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_userscontract, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int =  mClientListFiltered.size

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

            binding.mTvUserName.setText(mClientListFiltered[position].name)
            binding.mTvUserEmail.setText(mClientListFiltered[position].email)
            binding.mTvUserAddress.setText(mClientListFiltered[position].address.street+", "+mClientListFiltered[position].address.city)

            if (mClientListFiltered[position].trade.equals("admin")){
                binding.mTvUserStatus.setText(" Admin ")
            }else if (mClientListFiltered[position].trade.equals("manager")){
                binding.mTvUserStatus.setText(" Manager ")
            }else if (mClientListFiltered[position].trade.equals("employee")){
                binding.mTvUserStatus.setText(" Employee ")
            }else{
                binding.mTvUserStatus.setText(mClientListFiltered[position].trade.capitalize())
            }

            binding.mTvUserPhone.setText(insertString(mClientListFiltered[position].phone_no,"",0))
            binding.mTvUserPhone.setText(insertString(binding.mTvUserPhone.text.toString(),")",2))
            binding.mTvUserPhone.setText(insertString(binding.mTvUserPhone.text.toString()," ",3))
            binding.mTvUserPhone.setText(insertString(binding.mTvUserPhone.text.toString(),"-",7))
            binding.mTvUserPhone.setText("+1 ("+binding.mTvUserPhone.text.toString())

            binding.mTvUserEdit.setOnClickListener {
                listener.onSingleListClick("Edit", position)
            }

            binding.main.setOnClickListener {
                listener.onSingleListClick("main", position)
            }

            binding.main.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                return@setOnLongClickListener true
            }

        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            var result= FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty())
                {
//                    mClientListFiltered=mTasksData
                    result.count=mClientListOriginal.size
                    result.values=mClientListOriginal
                    return result
                }else{
                    val listing= mutableListOf<Client>()
                    mClientListOriginal.forEach {
                        if (it.name.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                Locale.ENGLISH))||
                            it.trade.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                Locale.ENGLISH))
                            || it.designation.toLowerCase(Locale.ENGLISH).contains(p0.toString().toLowerCase(
                                Locale.ENGLISH))){

                            listing.add(it)
                        }
                    }
                    result.count=listing.size
                    result.values=listing
                    return result
                }
            }
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                mClientListFiltered= p1!!.values as MutableList<Client>
                notifyDataSetChanged()
            }
        }
    }
}