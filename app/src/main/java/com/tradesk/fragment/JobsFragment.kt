package com.tradesk.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.LeadsData
import com.tradesk.R
import com.tradesk.activity.SettingsActivity
import com.tradesk.activity.jobModule.AddJobActivity
import com.tradesk.activity.jobModule.JobDetailActivity
import com.tradesk.activity.leadModule.AddLeadActivity
import com.tradesk.adapter.JobsAdapter
import com.tradesk.databinding.FragmentJobsBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class JobsFragment : Fragment() , SingleListCLickListener, LongClickListener {
    @Inject
    lateinit var mPrefs: PreferenceHelper
    @Inject
    lateinit var permissionFile: PermissionFile
    lateinit var binding: FragmentJobsBinding
    lateinit var viewModel: JobsViewModel
    var clicked = ""
    var mHomeImage = true
    var home=false
    var CheckVersion = true
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }
    var mList = mutableListOf<LeadsData>()
    val mJobsAdapter by lazy { JobsAdapter(requireActivity(), this,this,this, mList,mList) }
    var status_api = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProvider(requireActivity()).get(JobsViewModel::class.java)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentJobsBinding.inflate(inflater, container, false)
        setUp()
        initObserve()
        return binding.getRoot()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUp() {
        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            binding.mIvAddJobs.visibility = View.VISIBLE
        } else {
            binding.mIvAddJobs.visibility = View.GONE
        }
        val firstTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        firstTab.text = "All Jobs"
        binding.simpleTabLayout.addTab(firstTab)


        val secTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        secTab.text = "Pending"
        binding.simpleTabLayout.addTab(secTab)

        val thirdTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        thirdTab.text = "Ongoing"
        binding.simpleTabLayout.addTab(thirdTab)

        val forthTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        forthTab.text = "Complete"
        binding.simpleTabLayout.addTab(forthTab)
        binding.mEtSearchName.addWatcher {
            mJobsAdapter.filter.filter(it)
        }
        binding.simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        clicked = "0x"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        binding.mEtSearchName.setText("")
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                               viewModel.getAllLeads("job","1", "130", "all")
                            }
                        }
                    }
                    1 -> {
                        clicked = "1"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        binding.mEtSearchName.setText("")
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.getPendingLeads("job","1", "130", "pending")
                            }
                        }
                    }
                    2 -> {
                        clicked = "2"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        binding.mEtSearchName.setText("")
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.getOngoingLeads("job","1", "130", "ongoing")
                            }
                        }
                    }
                    3 -> {
                        clicked = "3"
                        mList.clear()
                        mJobsAdapter.notifyDataSetChanged()
                        binding.mEtSearchName.setText("")
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.getCompletedLeads("job","1", "130", "completed")
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.mIvAddJob.setOnClickListener {
            startActivity(Intent(requireActivity(),
                SettingsActivity::class.java)
            )
        }
        binding.mIvAddJobs.setOnClickListener {
            startActivity(
                Intent(activity, AddJobActivity::class.java))
        }
//        binding.mTvDateFilter.setOnClickListener { showLogoutMenu(mTvDateFilter, 1) }

        binding.rvJobs.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.rvJobs.adapter = mJobsAdapter

        Constants.showLoading(requireActivity())
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllLeads("job","1", "100", "all")
        }

    }

    fun initObserve()
    {
        viewModel.responseProfileModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mPrefs.setKeyValue(PreferenceConstants.USER_NAME, it.data!!.data.name)
                    mPrefs.setKeyValue(PreferenceConstants.USER_ID, it.data.data._id)
                    mPrefs.setKeyValue(PreferenceConstants.USER_LOGO, it.data.data.image)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseAllLeadsModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.leadsData)
                    mJobsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responsePendingLeadsModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.leadsData)
                    mJobsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseOngoingLeadsModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.leadsData)
                    mJobsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseCompletedLeadsModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.leadsData)
                    mJobsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    onResume()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseLeadDetailModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    val builder = GsonBuilder()
                    val gson = builder.create()
                    var string=gson.toJson(it.data!!)
                    try {
                        var activity=activity
                        if(activity!= null && home){
                            startActivity(Intent(requireContext(), AddJobActivity::class.java)
                                .putExtra("edit",true)
                                .putExtra("lead",string))
                        }
                    }
                    catch (e:Exception)
                    {

                    }


                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseJobConvertSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message)
                    onResume()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item == "2") {
            val lat = mList[position].address.location.coordinates[0]
            val long = mList[position].address.location.coordinates[1]
            val uri = "geo:${lat},${long}?q=${lat},${long}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)

        }
        else if (item == "3")
        {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + "+1 " + mList[position].client[0].phone_no)
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }
        else if (item == "4")
        {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "+1 " + mList[position].client[0].phone_no)
            startActivity(dialIntent)
        } else {
            Log.e("Total data",mList.get(position).toString())
            requireActivity().startActivity(
                Intent(requireActivity(), JobDetailActivity::class.java)
                    .putExtra("id", mList.get(position)._id)
            )
        }
    }

    override fun onLongClickListener(item: Any, position: Int) {
        showRightMenuOnLong(item as View,position)
    }
    private fun showRightMenuOnLong(anchor: View,selectedPosition:Int): Boolean {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_download).isVisible = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

        popup.setOnDismissListener {
//            if (selectedPosition != null) {
//                selectedIdArray.removeAll(setOf(mList[selectedPosition]._id))
//            }
        }

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (selectedPosition != null ) {
                    shareData(selectedPosition)
                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_delete) {
                if (isInternetConnected(requireActivity())) {
                    status_api = "Cancel Job"
                    Constants.showLoading(requireActivity())
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.convertJobs(mList.get(selectedPosition)._id, "job", "cancel", "false")
                    }

                }
            }
            else if (it.itemId == R.id.item_edit) {
                if (isInternetConnected(requireActivity())) {
                    Constants.showLoading(requireActivity())
                    home=true
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getLeadDetail(mList.get(selectedPosition)._id)
                    }
                }
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun shareData(selectedPosition: Int) {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nJob Detail")
        var phone = Constants.insertString(mList.get(selectedPosition).client.get(0).phone_no, "", 0)
        phone = Constants.insertString(phone!!, ")", 2)
        phone = Constants.insertString(phone!!, " ", 3)
        phone = Constants.insertString(phone!!, "-", 7)
        phone = "+1(" + phone!!
        var shareMessage = """
                    ${
            "\n" + "Job Detail" + "\n" + mList.get(selectedPosition).project_name + "\n" +
                    mList.get(selectedPosition).client.get(0).name + "\n" +
                    mList.get(selectedPosition).client.get(0).email + "\n" +
                    phone+ "\n" +
                    mList.get(selectedPosition).address.street+ "\n"
        }
               
                    """.trimIndent()

        shareMessage = """ $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}""".trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "choose one"))


//        if (selectedPosition!=null) {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
//            var shareMessage = """
//                    ${
//                "\n" + mList[selectedPosition].name + "\n" +
//                        mList[selectedPosition].email + "\n" +
//                        mList[selectedPosition].phone_no + "\n"
//            }
//               """.trimIndent()
//            shareMessage = """
//                    $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}
//                    """.trimIndent()
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
//            startActivity(Intent.createChooser(shareIntent, "choose one"))
//        }
    }

    override fun onResume() {
        super.onResume()
        home=false
        binding.mEtSearchName.setText("")
        if (clicked == "0") {
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getAllLeads("job","1", "190", "all")
                }
            }
        } else if (clicked == "1") {
            clicked = "1"
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getPendingLeads("job","1", "130", "pending")
                }
            }
        } else if (clicked == "2") {
            clicked = "2"
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getOngoingLeads("job","1", "130", "ongoing")
                }
            }
        } else if (clicked == "3") {
            clicked = "3"
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getCompletedLeads("job","1", "130", "completed")
                }
            }
        }
        else {
            mList.clear()
            mJobsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getAllLeads("job","1", "130", "all")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clicked = "0"
    }

}