package com.tradesk.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.CheckModel
import com.tradesk.Model.Expenses
import com.tradesk.R
import com.tradesk.databinding.RowItemExpensesBinding
import com.tradesk.databinding.RowItemNoteslistBinding
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ExpensesAdapter(
    context: Context,
    var mExpensesList: MutableList<Expenses>, val listener: SingleListCLickListener,
    val longClickListener: LongClickListener,
    var customCheckBoxListener: CustomCheckBoxListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
    var checkboxVisibility:Boolean,
    var allCheckBoxSelect:Boolean,
    var mcheckBoxModelList: MutableList<CheckModel>
) : RecyclerView.Adapter<ExpensesAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemExpensesBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemExpensesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_expenses, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mExpensesList.size

    @RequiresApi(Build.VERSION_CODES.O)
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

            binding.mTvClientName.text=mExpensesList[position].title
            binding.mTvDate.text=convertDateFormat(mExpensesList[position].createdAt)

            val formatter = DecimalFormat("#,###,###")
            binding.mTvTotal.setText("$ "+formatter.format(mExpensesList[position].amount.toInt()))

            if (mExpensesList.get(position).image.isNotEmpty()){
                binding.imageView13.loadWallImage(mExpensesList[position].image)
            }
            binding.imageView13.setOnClickListener {
                listener.onSingleListClick("Image", position)
            }
            binding.clParent.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                true
            }
            itemView.setOnClickListener {
                listener.onSingleListClick(mExpensesList[position], position)
            }
        }
    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("MMM dd, yyyy hh:mm", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}