package com.tradesk.activity.jobModule.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.Note
import com.tradesk.Model.Task
import com.tradesk.R
import com.tradesk.adapter.LeadsNotesAdapter
import com.tradesk.adapter.LeadsTasksAdapter
import com.tradesk.databinding.ActivityLeadNotesBinding
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
class LeadNotesActivity : AppCompatActivity() , SingleItemCLickListener, CustomCheckBoxListener {
    val mList = mutableListOf<Note>()
    val mListTask = mutableListOf<Task>()
    val mLeadsNotesAdapter by lazy { LeadsNotesAdapter(this, this, mList,mList) }
    val mLeadsTasksAdapter by lazy { LeadsTasksAdapter(this, this, this,mListTask,mListTask) }

    lateinit var binding:ActivityLeadNotesBinding
    lateinit var viewModel:JobsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_lead_notes)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)

        val firstTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        firstTab.text = "Notes"
        binding.simpleTabLayout.addTab(firstTab)

        binding.etSearch.addWatcher {
            mLeadsNotesAdapter.filter.filter(it)
        }

        val secTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        secTab.text = "Tasks"
        binding.simpleTabLayout.addTab(secTab)

        binding.rvNotesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvNotesList.adapter = mLeadsNotesAdapter


        binding.simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.rvNotesList.adapter = mLeadsNotesAdapter
                    }
                    1 -> {
                        binding.rvNotesList.adapter = mLeadsTasksAdapter
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.leadsnoteslist(intent.getStringExtra("id").toString())
            }
        }

        binding.mIvBack.setOnClickListener { finish() }
        binding.mIvAddNotes.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddNotesActivity::class.java
                ).putExtra("id", intent.getStringExtra("id").toString())
            )
        }
        binding.mIvAdd.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddNotesActivity::class.java
                ).putExtra("id", intent.getStringExtra("id").toString())
            )
        }

        initObserve()
    }

    fun initObserve() {
        viewModel. responseNotesListModel.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.notes)
                    mLeadsNotesAdapter.notifyDataSetChanged()
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

    override fun onSingleItemClick(item: Any, position: Int) {

    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.leadsnoteslist(intent.getStringExtra("id").toString())
            }
        }
    }
}