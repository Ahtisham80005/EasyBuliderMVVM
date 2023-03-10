package com.tradesk.activity.calendarModule

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.R
import com.tradesk.adapter.CustomersAdapter
import com.tradesk.databinding.CustomCalendarDayBinding
import com.tradesk.databinding.RowItemCustomerslistBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val context: Context,
                      private val data: ArrayList<Date>,
                      private val currentDate: Calendar,
                      private val changeMonth: Calendar?): RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {
    private var mListener: OnItemClickListener? = null
    private var index = -1
    // This is true only the first time you load the calendar.
    private var selectCurrentDate = true
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val selectedDay =
        when {
            changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
            else -> currentDay
        }
    private val selectedMonth =
        when {
            changeMonth != null -> changeMonth[Calendar.MONTH]
            else -> currentMonth
    }
    private val selectedYear =
        when {
            changeMonth != null -> changeMonth[Calendar.YEAR]
            else -> currentYear
        }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyViewHolder {
        val binding: CustomCalendarDayBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.custom_calendar_day, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.time = data[position]

        /**
         * Set the year, month and day that is gonna be displayed
         */
        val displayMonth = cal[Calendar.MONTH]
        val displayYear= cal[Calendar.YEAR]
        val displayDay = cal[Calendar.DAY_OF_MONTH]

        /**
         * Set text to txtDayInWeek and txtDay.
         */
        try {
            val dayInWeek = sdf.parse(cal.time.toString())!!
            sdf.applyPattern("EEE")
            holder.binding.txtDay!!.text = sdf.format(dayInWeek).toString()
        } catch (ex: ParseException) {
            Log.v("Exception", ex.localizedMessage!!)
        }
//        holder.txtDay.visibility=View.VISIBLE
        holder.binding.txtDate!!.text = cal[Calendar.DAY_OF_MONTH].toString()

        /**
         * I think you can use "cal.after (currentDate)" and "cal == currentDate",
         * but it didn't work properly for me, so I used this longer version. Here I just ask
         * if the displayed date is after the current date or if it is current date, if so,
         * then you enable the item and it is possible to click on it, otherwise deactivate it.
         * The selectCurrentDate value is valid only at the beginning, it will be the current
         * day or the first day, for example when starting the application or changing the month.
         */
//        if (displayYear >= currentYear)
//            if (displayMonth >= currentMonth || displayYear > currentYear)
//                if (displayDay >= currentDay || displayMonth > currentMonth || displayYear > currentYear) {
                    /**
                     * Invoke OnClickListener and make the item selected.
                     */
                         holder.binding.calendarLinearLayout!!.setOnClickListener {
                        index = position
                        selectCurrentDate = false
//                        holder.listener.onItemClick(position)
                        notifyDataSetChanged()
                    }

                    if (index == position)
                        makeItemSelected(holder)
                    else {
                        if (displayDay == selectedDay
                            && displayMonth == selectedMonth
                            && displayYear == selectedYear
                            && selectCurrentDate) {
                            makeItemSelected(holder)
                            index = position
                            selectCurrentDate = false
//                            holder.listener.onItemClick(position)
                        }
                        else
                            makeItemDefault(holder)
                    }
//                } else makeItemDisabled(holder)
//            else makeItemDisabled(holder)
//        else makeItemDisabled(holder)
    }

    class MyViewHolder (var binding: CustomCalendarDayBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }

//    inner class ViewHolder(itemView: View, val listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
//        var txtDay = itemView.txt_date
//        var txtDayInWeek = itemView.txt_day
//        var linearLayout = itemView.calendar_linear_layout
//    }

    /**
     * OnClickListener.
     */
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    /**
     * This make the item disabled.
     */
    private fun makeItemDisabled(holder: MyViewHolder) {
        holder.binding.txtDate!!.setTextColor(ContextCompat.getColor(context, R.color.text_color))
        holder.binding.txtDay!!.setTextColor(ContextCompat.getColor(context, R.color.black_color))
        holder.binding.calendarLinearLayout!!.setBackgroundColor(Color.WHITE)
        holder.binding.calendarLinearLayout!!.isEnabled = false
    }

    /**
     * This make the item selected.
     */
    private fun makeItemSelected(holder: MyViewHolder) {
        holder.binding.txtDate!!.setTextColor(Color.parseColor("#FFFFFF"))
        holder.binding.txtDay!!.setTextColor(Color.parseColor("#FFFFFF"))
        holder.binding.calendarLinearLayout!!.setBackgroundResource(R.drawable.ic_oramgeround)
        holder.binding.calendarLinearLayout!!.isEnabled = false
    }

    /**
     * This make the item default.
     */
    private fun makeItemDefault(holder: MyViewHolder) {
        holder.binding.txtDate!!.setTextColor(Color.BLACK)
        holder.binding.txtDay!!.setTextColor(Color.BLACK)
        holder.binding.calendarLinearLayout!!.setBackgroundColor(Color.WHITE)
        holder.binding.calendarLinearLayout!!.isEnabled = true
    }
}
