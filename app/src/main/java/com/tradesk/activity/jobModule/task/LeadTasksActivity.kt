package com.tradesk.activity.jobModule.task

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.Task
import com.tradesk.R
import com.tradesk.adapter.LeadsTasksAdapter
import com.tradesk.databinding.ActivityLeadTasksBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeadTasksActivity : AppCompatActivity() , SingleItemCLickListener, CustomCheckBoxListener {
    val mList = mutableListOf<Task>()
    val mLeadsTasksAdapter by lazy { LeadsTasksAdapter(this, this, this,mList,mList) }
    lateinit var binding:ActivityLeadTasksBinding
    lateinit var viewModel:JobsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_lead_tasks)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)
        binding.etSearch.addWatcher {
            mLeadsTasksAdapter.filter.filter(it)
        }

        binding.rvTasksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvTasksList.adapter = mLeadsTasksAdapter

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.leadstaskslist(intent.getStringExtra("id").toString())
            }
        }

        binding.mIvBack.setOnClickListener { finish() }
        binding.mIvAddTasks.setOnClickListener {
            startActivity(
                Intent(this, AddTasksActivity::class.java).putExtra("id", intent.getStringExtra("id").toString())
            )
        }
        binding.mIvAdd.setOnClickListener {
            startActivity(
                Intent(
                    this, AddTasksActivity::class.java).putExtra("id", intent.getStringExtra("id").toString())
            )
        }
        initObserve()
    }

    fun initObserve() {
        viewModel.responseTaskModel.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.task)
                    mLeadsTasksAdapter.notifyDataSetChanged()
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

    override fun onCheckBoxClick(item: Int) {
        AllinOneDialog(getString(R.string.app_name),
            "Do you want to complete this task ?",
            btnLeft = "No",
            btnRight = "Yes",
            onLeftClick = {},
            onRightClick = {})
    }

    override fun onResume() {
        super.onResume()
        Constants.showLoading(this)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.leadstaskslist(intent.getStringExtra("id").toString())
        }
    }

    override fun onSingleItemClick(item: Any, position: Int) {

    }
}