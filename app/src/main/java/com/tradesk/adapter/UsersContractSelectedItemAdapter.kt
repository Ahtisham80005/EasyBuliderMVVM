package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Client
import com.tradesk.R
import com.tradesk.databinding.RowItemUserscontractBinding
import com.tradesk.databinding.RowSelectUserscontractBinding
import com.tradesk.util.Constants

class UsersContractSelectedItemAdapter(
    context: Context, var selectType:String,
    var mClientListFiltered: MutableList<Client>,
    var mClientListOriginal: MutableList<Client>,
    val listener: SingleListCLickListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener
) : RecyclerView.Adapter<UsersContractSelectedItemAdapter.MyViewHolder>(){

    var incr = 0
    var decr = 0

    class MyViewHolder (var binding: RowSelectUserscontractBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowSelectUserscontractBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_select_userscontract, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int =  mClientListFiltered.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            if (mClientListFiltered[position].image.isNotEmpty()) {
                binding.mCvUserProfile.loadWallImage(mClientListFiltered[position].image)
            }
            if (mClientListFiltered[position].image.isNotEmpty()) {
                binding.mCvUserProfile.loadWallImage(mClientListFiltered[position].image)
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

            binding.mTvUserPhone.setText(
                Constants.insertString(
                    mClientListFiltered[position].phone_no,
                    "",
                    0
                )
            )
            binding.mTvUserPhone.setText(
                Constants.insertString(
                    binding.mTvUserPhone.text.toString(),
                    ")",
                    2
                )
            )
            binding.mTvUserPhone.setText(
                Constants.insertString(
                    binding.mTvUserPhone.text.toString(),
                    " ",
                    3
                )
            )
            binding.mTvUserPhone.setText(
                Constants.insertString(
                    binding.mTvUserPhone.text.toString(),
                    "-",
                    7
                )
            )
            binding.mTvUserPhone.setText("+1 ("+binding.mTvUserPhone.text.toString())

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


//            tvEdit.setOnClickListener {
//                    listener.onSingleListClick("Edit", position)
//            }
//
//            main.setOnClickListener {
//                listener.onSingleListClick("main", position)
//            }


        }
    }
}