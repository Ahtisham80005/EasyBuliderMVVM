package com.tradesk.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.Task
import com.tradesk.R
import com.tradesk.databinding.RowItemProposalsBinding
import com.tradesk.databinding.RowItemTaskslistBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LeadsTasksAdapter(
    val context: Context,
    val listener: SingleItemCLickListener,
    val listenerTwo: CustomCheckBoxListener,
    var mNotesFiltered: MutableList<Task>,
    var mNotesOrginal: MutableList<Task>
) : RecyclerView.Adapter<LeadsTasksAdapter.MyViewHolder>(), Filterable {

    private var editor: SharedPreferences.Editor
    private var sharedPrefs: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    var visiblePos = -1

    init {
        editor = sharedPrefs.edit()
    }

    class MyViewHolder (var binding: RowItemTaskslistBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemTaskslistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_taskslist, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mNotesFiltered.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            binding.mTvTasksTitle.text = mNotesFiltered[position].title
            binding.mTvTasksDesc.text = mNotesFiltered[position].description
            binding.mTvDate.text = convertDateFormat(mNotesFiltered[position].createdAt)
            binding.mTvTime.text = convertTimeDateFormat(mNotesFiltered[position].createdAt)

            binding.tvStatus.setOnCheckedChangeListener(null)
            binding.tvStatus.isChecked = sharedPrefs.getBoolean("CheckValue" + position, false);

            binding.tvStatus.setOnCheckedChangeListener { _, isChecked ->
                editor.putBoolean("CheckValue" + position, isChecked);
                editor.commit()
            }

            binding.parent.setOnClickListener { listener.onSingleItemClick("1", position) }
        }
    }

    private fun convertDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
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

    private fun convertTimeDateFormat(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        spf.timeZone = TimeZone.getTimeZone("UTC")
        var newDate: Date? = Date()
        try {
            newDate = spf.parse(date)
//            spf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            spf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return spf.format(newDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            var result = FilterResults()
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if (p0!!.isEmpty()) {
//                    mTasksDataFiltered=mTasksData
                    result.count = mNotesOrginal.size
                    result.values = mNotesOrginal
                    return result
                } else {
                    val listing = mutableListOf<Task>()
                    mNotesOrginal.forEach {
                        if (it.title.lowercase(Locale.ENGLISH)
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
                mNotesFiltered = p1!!.values as MutableList<Task>
                notifyDataSetChanged()
            }

        }
    }
}