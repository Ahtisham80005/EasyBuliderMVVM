package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.DailyTimeLogNewTimeSheet
import com.tradesk.R
import com.tradesk.databinding.RowItemDayslisttimesheetBinding
import com.tradesk.databinding.RowItemTimesheetdetailBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimeSheetDetailAdapter(
    context: Context,
    val listener: SingleListCLickListener,
    var mTasksDataFiltered: MutableList<DailyTimeLogNewTimeSheet>) : RecyclerView.Adapter<TimeSheetDetailAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemTimesheetdetailBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemTimesheetdetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_timesheetdetail, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int =  mTasksDataFiltered.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {

            var mins =mTasksDataFiltered[position].log_time.toString().substringAfter(":")
//            mTvJobTime.text=mTasksDataFiltered[position].total_time.toString().substringBefore(":")+" hrs"+mins.toString().substringBefore(":")+" min"

            binding.mTvJobTime.setText(mTasksDataFiltered[position].log_time.toString().substring(0,2)
                    +"h "+mTasksDataFiltered[position].log_time.toString().substring(3,5)+"m "+mTasksDataFiltered[position].log_time.toString().substring(6,8)+"s")


            binding.mTvInAddress.setText(mTasksDataFiltered[position].clockedIn_address.street+", "+mTasksDataFiltered[position].clockedIn_address.city)

            binding.mTvDate.text=convertDateFormat(mTasksDataFiltered[position].start_date)
            binding.mTvInTime.text=convertTimeDateFormat(mTasksDataFiltered[position].start_date)
            if (mTasksDataFiltered[position].end_date!=null){
                binding.mTvOutTime.text=convertTimeDateFormat(mTasksDataFiltered[position].end_date.toString())
                binding.mTvOutAddress.text=mTasksDataFiltered[position].clockedOut_address.street+", "+mTasksDataFiltered[position].clockedOut_address.city
            }else{
                binding.mTvOutTime.text="N/A"
                binding.mTvOutAddress.text="N/A"
            }
            binding.parent.setOnClickListener { listener.onSingleListClick("Detail",position) }
        }
    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("MMM dd, yy", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun convertTimeDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}