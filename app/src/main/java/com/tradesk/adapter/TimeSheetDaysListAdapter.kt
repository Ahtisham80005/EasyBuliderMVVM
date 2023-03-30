package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.JobLogTimeNewTimeSheet
import com.tradesk.R
import com.tradesk.databinding.RowItemDayslisttimesheetBinding
import com.tradesk.databinding.RowItemTaskslistBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeSheetDaysListAdapter(
    context: Context,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<JobLogTimeNewTimeSheet>) :
    RecyclerView.Adapter<TimeSheetDaysListAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemDayslisttimesheetBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemDayslisttimesheetBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_dayslisttimesheet, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =  mTasksDataFiltered.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {

            var mins =mTasksDataFiltered[position].total_time.toString().substringAfter(":")
//            mTvJobTime.text=mTasksDataFiltered[position].total_time.toString().substringBefore(":")+" hrs"+mins.toString().substringBefore(":")+" min"

            binding.mTvJobTime.setText(mTasksDataFiltered[position].total_time.toString().substring(0,2)
                    +"h "+mTasksDataFiltered[position].total_time.toString().substring(3,5)+"m "+mTasksDataFiltered[position].total_time.toString().substring(6,8)+"s")

            binding.mTvDate.text=convertDateFormat(mTasksDataFiltered[position].start_date)

            binding.parent.setOnClickListener { listener.onSingleListClick("Detail",position) }
            binding.mTvView.setOnClickListener { listener.onSingleListClick("Detail",position) }
        }
    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}