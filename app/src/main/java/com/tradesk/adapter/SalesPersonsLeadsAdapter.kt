package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.Client
import com.tradesk.R
import com.tradesk.databinding.AddClientListItemBinding
import com.tradesk.databinding.RowItemSalespersonsBinding
import com.tradesk.util.Constants.insertString

class SalesPersonsLeadsAdapter (
    context: Context,
    var mClients: MutableList<Client>, val listener: SingleListCLickListener
) : RecyclerView.Adapter<SalesPersonsLeadsAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemSalespersonsBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemSalespersonsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_salespersons, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =  mClients.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            if (mClients.get(position).image.isNotEmpty()) {
                binding.mIvPic.loadWallImage(mClients.get(position).image)
            }
            binding.mTvName.text = mClients[position].name
            binding.mTvEmail.text = mClients[position].email
            binding.mTvAddress.text =
                mClients[position].address.city + ", " + mClients[position].address.state

            binding.mTvPhone.text = insertString(mClients[position].phone_no.replace(" ", ""), "", 0)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
            binding.mTvPhone.text = "(" + binding.mTvPhone.text.toString()

            binding.tvStatus.setOnCheckedChangeListener { compoundButton, b ->
                listener.onSingleListClick(
                    "1",
                    position
                )
            }
        }
    }
}