package com.tradesk.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Expenses
import com.tradesk.R
import com.tradesk.databinding.RowItemExpensesSlectedBinding
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ExpensesSelectItemAdapter (
    var context: Context, var selectType:String,
    var mExpensesList: MutableList<Expenses>,
    val listener: SingleListCLickListener,
    val unselectCheckBoxListener: UnselectCheckBoxListener,
    val longClickListener: LongClickListener
) : RecyclerView.Adapter<ExpensesSelectItemAdapter.MyViewHolder>() {

    var incr = 0
    var decr = 0
    class MyViewHolder (var binding: RowItemExpensesSlectedBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemExpensesSlectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_expenses_slected, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mExpensesList.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            binding.mTvClientName.text=mExpensesList[position].title
            binding.mTvDate.text=convertDateFormat(mExpensesList[position].createdAt)
            val formatter = DecimalFormat("#,###,###")
            binding.mTvTotal.setText("$ "+formatter.format(mExpensesList[position].amount.toInt()))
            if (mExpensesList.get(position).image.isNotEmpty()){
                binding.imageView13.loadWallImage(mExpensesList[position].image)
            }
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
            binding.clParent.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                true
            }

            if (selectType.equals("all")){
                binding.mCheckBok.isChecked = true
                binding.mCheckBok.isClickable = false
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