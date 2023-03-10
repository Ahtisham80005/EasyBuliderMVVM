package com.tradesk.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.BuildzerApp
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.RemainderData
import com.tradesk.R
import com.tradesk.databinding.RowItemCalendaritemsBinding
import com.tradesk.databinding.RowItemCustomerslistBinding
import com.tradesk.util.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CalendarItemsAdapter(
    val context: Context,
    val listener: SingleItemCLickListener,
    val longClickListener: LongClickListener,
    var mTasksDataFiltered: MutableList<RemainderData>) : RecyclerView.Adapter<CalendarItemsAdapter.MyViewHolder>() {

    class MyViewHolder (var binding: RowItemCalendaritemsBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    var visiblePos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemCalendaritemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_calendaritems, parent, false)
        return MyViewHolder(binding)
    }
    override fun getItemCount(): Int =  mTasksDataFiltered.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.apply {
            if (mTasksDataFiltered[position].toString().equals("0")){
                binding.mTvDate.visibility= View.VISIBLE
            }else{
                binding.mTvDate.visibility= View.GONE
            }

            if (mTasksDataFiltered[position].remainder_type.equals("phone")){
                binding.imageView54.setImageDrawable(context.getDrawable(R.drawable.ic_callphone))
            }else{
                binding.imageView54.setImageDrawable(context.getDrawable(R.drawable.ic_calendarornagecolr))
            }

            if (mTasksDataFiltered[position].type.equals("note"))
            {
                binding.mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)
                binding.mTvName.text=mTasksDataFiltered[position].description
                binding.mTvtime.text=convertDateFormatWithTime(mTasksDataFiltered[position].dateTime)
            }
            else
            {
                if (mTasksDataFiltered[position].client_id!=null&&mTasksDataFiltered[position].remainder_type.equals("phone"))
                {
                    binding.mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)
                    binding.mTvName.text="Call with "+mTasksDataFiltered[position].client_id!!.name
                    binding.mTvtime.text=convertDateFormatWithTime(mTasksDataFiltered[position].dateTime)
                }
                else if (mTasksDataFiltered[position].client_id!=null)
                {
                    binding.mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)
                    binding.mTvName.text="Appointment with "+mTasksDataFiltered[position].client_id!!.name
                    binding.mTvtime.text=convertDateFormatWithTime(mTasksDataFiltered[position].dateTime)
                }
                else{
                    binding.mTvDate.text=convertDateFormat(mTasksDataFiltered[position].dateTime)
                    binding.mTvName.text=mTasksDataFiltered[position].description
//                    mTvtime.text=convertDateFormatWithTime("2023-01-27T21:22:00.000Z")
                    binding.mTvtime.text=convertDateFormatWithTime(mTasksDataFiltered[position].dateTime)
                }
            }

//            mTvtime.text=convertDateFormatWithTime("2023-01-27T21:22:00.000Z")

            binding.mIvRightMenu.setOnClickListener {
                listener.onSingleItemClick(binding.mIvRightMenu,position)
            }

            binding.clParent.setOnLongClickListener { longClick->
                longClickListener.onLongClickListener(longClick,position)
                true
            }
        }
    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertDateFormatWithTime(date: String?): String? {

//    var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm aa", Locale.getDefault())
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        var newDate: Date? = Date()
        try {
            if(date!=null)
            {
                newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                spf = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.getDefault())
                return spf.format(newDate)
            }
            else
            {
                return ""
            }
        } catch (e: ParseException) {
            Log.e("Date Execption",e.message.toString())
            e.printStackTrace()
        }
        return ""
    }
}