package com.tradesk.activity.timesheetModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.DailyTimeLogNewTimeSheet
import com.tradesk.R
import com.tradesk.adapter.TimeSheetDetailAdapter
import com.tradesk.databinding.ActivityTimesheetBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimesheetActivity : AppCompatActivity() , SingleListCLickListener {

    val mList = mutableListOf<DailyTimeLogNewTimeSheet>()
    val mTimeSheetAdapter by lazy { TimeSheetDetailAdapter(this,this,mList) }
    lateinit var binding:ActivityTimesheetBinding
    lateinit var viewModel: JobsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_timesheet)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)
        binding.rvTimeSheet.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvTimeSheet.adapter = mTimeSheetAdapter

        binding.mIvBack.setOnClickListener { finish() }
        if (isInternetConnected(this)){
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.jobsdetailtimesheet(intent.getStringExtra("id").toString())
            }
        }
    }

    fun initObserve() {
        viewModel.responseJobDetailTimesheet.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    for (i in 0 until it.data!!.data.job_details[0].job_log_time.size) {
                        if (it.data!!.data.job_details[0].job_log_time[i].start_date.equals(intent.getStringExtra("date"))){
                            mList.clear()
                            mList.addAll(it.data!!.data.job_details[0].job_log_time[i].daily_time_log)
                        }
                    }
                    mTimeSheetAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading -> {
                    Constants.showLoading(this)
                }
            }
        })
    }
    override fun onSingleListClick(item: Any, position: Int) {

    }
}