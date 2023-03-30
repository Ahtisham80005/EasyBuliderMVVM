package com.tradesk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.Note
import com.tradesk.R
import com.tradesk.databinding.RowItemNoteslistBinding
import com.tradesk.databinding.RowItemTaskslistBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LeadsNotesAdapter(

    context: Context,
    val listener: SingleItemCLickListener,
    var mNotesFiltered: MutableList<Note>,
    var mNotesOrginal: MutableList<Note>
) : RecyclerView.Adapter<LeadsNotesAdapter.MyViewHolder>(),
    Filterable {

    class MyViewHolder (var binding: RowItemNoteslistBinding): RecyclerView.ViewHolder((binding.root))
    {
        fun bind() {
//            binding.model=model
            binding.executePendingBindings()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowItemNoteslistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_item_noteslist, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = mNotesFiltered.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            binding.mTvNoteTitle.text = mNotesFiltered[position].title
            binding.mTvNoteDesc.text = mNotesFiltered[position].description
            binding.mTvDate.text = convertDateFormat(mNotesFiltered[position].createdAt)
            binding.mTvTime.text = convertTimeDateFormat(mNotesFiltered[position].createdAt)
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
                    val listing = mutableListOf<Note>()
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
                mNotesFiltered = p1!!.values as MutableList<Note>
                notifyDataSetChanged()
            }
        }
    }
}