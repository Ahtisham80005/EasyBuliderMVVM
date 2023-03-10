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
import com.tradesk.Interface.SingleItemCLickListener
import com.tradesk.Model.LeadsData
import com.tradesk.R
import com.tradesk.activity.auth.LoginActivity
import com.tradesk.activity.leadModule.AddLeadActivity
import com.tradesk.activity.leadModule.LeadDetailActivity
import com.tradesk.adapter.HomeLeadsAdapter
import com.tradesk.databinding.FragmentHomeBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.PermissionFile
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.LeadViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class HomeFragment : Fragment(),SingleItemCLickListener,LongClickListener {

    @Inject
    lateinit var mPrefs: PreferenceHelper
    @Inject
    lateinit var permissionFile: PermissionFile
    var mHomeImage = true
    var CheckVersion = true
    var clicked = ""
    val isPortalUser by lazy {
        mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).contains("charity").not()
    }

    var mList = mutableListOf<LeadsData>()
    val mHomeLeadsAdapter by lazy { HomeLeadsAdapter(requireActivity(), this,this,this, mList,mList) }

    lateinit var binding:FragmentHomeBinding
    lateinit var viewModel:LeadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=ViewModelProvider(requireActivity()).get(LeadViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater, container, false)
        Constants.showLoading(requireActivity())
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getAllLeads("lead","1", "200", "all")
        }
        setUp()
        initObserve()
        return binding.getRoot()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUp() {
        if (mPrefs.getKeyValue(PreferenceConstants.USER_TYPE).equals("1")) {
            binding.mIvAdd.visibility = View.VISIBLE
        } else {
            binding.mIvAdd.visibility = View.GONE
        }
        val firstTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        firstTab.text = "All Leads"
        binding.simpleTabLayout.addTab(firstTab)


        val secTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        secTab.text = "Pending"
        binding.simpleTabLayout.addTab(secTab)


        val thirdTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        thirdTab.text = "Follow Up"
        binding.simpleTabLayout.addTab(thirdTab)


        val forthTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        forthTab.text = "Sale"
        binding.simpleTabLayout.addTab(forthTab)


        val decodedBytes = Base64.getDecoder().decode(
            "UGVybWl0cyAmIEluc3BlY3Rpb25zIApDb21wYW55IHRvIG9idGFpbiBuZWNlc3NhcnkgY2l0eSBwZXJtaXRzIGFzIHdlbGwgYXMgc2NoZWR1bGUgYWxsIGNpdHkgaW5zcGVjdGlvbnMgcmVxdWlyZWQgZm9yIGJhdGhyb29tIHJlbW9kZWw7CsKgCsKgCuKXjyBBcmNoaXRlY3R1cmFsIGFuZCBlbmdpbmVlcmluZyBwbGFucyAoQmx1ZXByaW50cyk6IApbT3B0aW9uYWxdIENvbXBhbnkgd2lsbCBwcmVwYXJlIGFuZCBwcm92aWRlIHRoZSBjdXN0b21lciB3aXRoIGFyY2hpdGVjdHVyYWwgYW5kIGVuZ2luZWVyaW5nIHBsYW5zIChCbHVlcHJpbnRzKSBmb3IgYSBsb2FkIGJlYXJpbmcgd2FsbCByZW1vdmFsIHRvIGJlIHN1Ym1pdHRlZCB0byB0aGUgY2l0eSB0byBvYnRhaW4gYSBwZXJtaXQgKENvbXBhbnkgZ3VhcmFudGVlIG9idGFpbmluZyBwZXJtaXRzKS4gQW55IGNvcnJlY3Rpb25zIG5lZWRlZCB3aWxsIG5vdCBjYXJyeSBhZGRpdGlvbmFsIGNoYXJnZS4gCsKgCsKgCsKgCsKgCkRlbW9saXRpb24gCkRlbW9saXNoIGFuZCByZW1vdmUgZXhpc3RpbmcgY2FiaW5ldHMsIGNvdW50ZXJ0b3BzLCBmbG9vciwgZml4dHVyZXMsIGV0Yy4gClNhbHZhZ2UgdGhlIGZvbGxvd2luZyBpdGVtcwrCoArCoAril48gbW9sZC9hc2Jlc3Rvcy9MZWFkIHRlc3QKY29tcGFueSBpcyByZWNvbW1lbmRhdGlvbjogCmN1c3RvbWVyIHRvIGhpcmVkIGEgM3JkIHBhcnR5IGxpY2Vuc2UgY29tcGFueSB0byBwcm92aWRlIG1vbGQgdGVzdCAKaWYgaG91c2UgYnVpbHQgYmVmb3JlIDE5NzggcmVxdWlyZWQgYnkgRVBBIGxhdzoKY3VzdG9tZXIgdG8gaGlyZWQgYSAzcmQgcGFydHkgbGljZW5zZSBjb21wYW55IHRvIHByb3ZpZGUgQXNiZXN0b3MgdGVzdCAKY3VzdG9tZXIgdG8gaGlyZWQgYSAzcmQgcGFydHkgbGljZW5zZSBjb21wYW55IHRvIHByb3ZpZGUgTEVBRCB0ZXN0IArCoArCoEZyYW1pbmcgCldhbGstaW4gc2hvd2VyIGZyYW1pbmc6IFtTdGFuZGFyZCA24oCdIGN1cmJlZCAvIFN0ZXBsZXNzXQpTaGFtcG9vIG5pY2hlOiBbU2VsZWN0OiBTdGFuZGFydCBiZXR3ZWVuIHN0dWRzIC8gQ3VzdG9tIHNpemVdIApTaG93ZXIgQmVuY2g6IFtTZWxlY3Q6IEZsb2F0aW5nIC8gTW91bnRlZCA7IFNlbGVjdDogQ29ybmVyIC8gUmVjdGFuZ3VsYXJdIApUdWIgZnJhbWluZzogW1NlbGVjdDogUmVndWxhciB0dWIgLyBGcmVlc3RhbmRpbmcgdHViIC8gRHJvcC1pbiB0dWIgZnJhbWVdCkFkZGl0aW9uYWwgZnJhbWluZyBpdGVtczogW1NlbGVjdDogUGV0aXRpb24gd2FsbHMgLyBQb255IHdhbGxzIC8gRG9vcnMgLyBXaW5kb3dzIC8gU29mZml0XSAKQ2FuY2VsYXRpb24gb2YgZXhpc3RpbmcgZnJhbWluZzogW1NlbGVjdDogU29mZml0cyAvIFdhbGxzIChwZXRpdGlvbi9sb2FkIGJlYXJpbmcpIC8gT3BlbmluZ3MgLyBXaW5kb3dzIC8gU29mZml0IC8gUmVjZXNzZWQgY2FiaW5ldF0uIArCoMKgwqAKUm91Z2ggZWxlY3RyaWNhbCAKVXBncmFkZSBhbmQgcHJlcGFyZSBlbGVjdHJpY2l0eSB1cCB0byBjb2RlIChOb3QgaW5jbHVkZXMgcmV3aXJlIC8gcGFuZWwgYm94IHVwZ3JhZGUpLiBDb21wYW55IHdpbGwgcHJvdmlkZSBhbmQgaW5zdGFsbCB0aGUgZm9sbG93aW5nIGl0ZW1zOiAKR0ZDSSByZWNlcHRhY2xlcyBbUXVhbnRpdHk6IF9dClN3aXRjaGVzIFtTZWxlY3Q6IHJlZ3VsYXIgLyBkaW1tZXIgOyBRdWFudGl0eTogX10KTEVEIHJlY2Vzc2VkIGxpZ2h0cyBbU2VsZWN0OiBTaXplIDTigJ0gLyA24oCdIC8gOOKAnSA7IFNlbGVjdDogQ29sZCAvIE5vcm1hbCAvIFdhcm0gV2hpdGUgOyBRdWFudGl0eTogX10KQWRkaXRpb25hbCBsaWdodCBwb3dlciBzcG90IFtRdWFudGl0eTogX107IApFeGhhdXN0IGZhbiAtIENvbXBhbnkgc2VsZWN0aW9uIFtRdWFudGl0eTogX107CkFkZGl0aW9uYWwgcm91Z2ggZWxlY3RyaWNhbCBpdGVtcyAoaS5nLiBVU0Igb3V0bGV0cywgRWxlY3RyaWNhbCBUb2lsZXQsIGV0Yy4pOgrCoApSb3VnaCBwbHVtYmluZyAKVXBncmFkZSBhbmQgcHJlcGFyZSBleGlzdGluZyBiYXRocm9vbSByb3VnaCBwbHVtYmluZyB1cCB0byBjb2RlIChyZXBsYWNlIOKAnG91dCBvZiB0aGUgd2FsbOKAnSBwaXBlcyB0byBuZXcgY29wcGVyL1BFWCBwaXBpbmcuIE5vdCBpbmNsdWRpbmcgcmUtcGlwZSAvIHJlLWRyYWluIC8gbmV3IGRyYWluKToKSW5zdGFsbCBTaG93ZXIgLyBUdWIgdmFsdmVzIHN5c3RlbSBbU2VsZWN0OiBTaW5nbGUgdmFsdmUgLyBEdWFsIHZhbHZlIC8gT3RoZXI6X10gCkluc3RhbGwgVHViIFtTZWxlY3Q6IHJlZ3VsYXIgdHViIC8gZnJlZXN0YW5kaW5nIHR1YiAvIGRyb3AtaW4gdHViXSAtIEN1c3RvbWVyIHByb3ZpZGVzCkluc3RhbGwgc2hvd2VyIGRyYWluIHN5c3RlbSBbU2VsZWN0OiBDZW50ZXJlZCBkcmFpbiAvIFR1YiBkcmFpbi8g4oCcaW5maW5pdHnigJ0gZHJhaW5dOyAKSW5zdGFsbCBiYXRocm9vbSB2YW5pdHkgcm91Z2ggcGx1bWJpbmcgW1NlbGVjdDogU2luZ2xlIHZhbml0eSAvIERvdWJsZSB2YW5pdHkgLyBPdGhlcjpfXTsKQWRkaXRpb25hbCByb3VnaCBwbHVtYmluZyBpdGVtcyAoaWYgYW55KSAKwqAKwqAKV2F0ZXJwcm9vZmluZyAmIHRpbGUgcHJlcGFyYXRpb25zCkluc3RhbGwgbGF0aCBwYXBlciBhbmQgd2lyZSBtZXNoIG9uIHNob3dlciB3YWxscy4KQXBwbHkgYSBob3QgbW9wIG9uIHNob3dlciBwYW4gYW5kIGZpbGwgd2l0aCB3YXRlciAoMjQtNDggaG91cnMpLiAKQXBwbHkgZmxvYXRpbmcgY2VtZW50L0NlbWVudCBib2FyZHMgKHNjcmF0Y2ggYW5kIGNlbWVudCBjb2F0KSBvbiBzaG93ZXIgd2FsbHMgdG8gcHJlcGFyZSBmb3IgdGlsZSBpbnN0YWxsYXRpb24uIApQb3VyIGNvbmNyZXRlIG9uIHNob3dlciBwYW5lLiAKwqAKwqAKVGlsZSBpbnN0YWxsYXRpb24KSW5zdGFsbCB0aWxlcyBpbiB0aGUgZm9sbG93aW5nIGFyZWFzIFtTcGVjaWZ5IGFweC4gU0YgZm9yIGVhY2ggaXRlbV06IApTaG93ZXIgd2FsbHM7IApTaG93ZXIgcGFuZTsgCkJhdGhyb29tIGZsb29yIFtPcHRpb25hbDogTFZQIGZsb29yaW5nIGluc3RlYWQgb2YgVGlsZV0KQWRkaXRpb25hbCB0aWxlIGFyZWFzIFtTZWxlY3Q6IFNoYW1wb28gbmljaGUgLyBCZW5jaCAvIFNob3dlciB0aHJlc2hvbGQgLyBCYXRocm9vbSB3YWxsc10gClRpbGUgZWRnZXIgW1NlbGVjdDogQnVsbG5vc2UgLyBNZXRhbCAvIE90aGVyXQpDdXN0b21lcnMgc2hhbGwgcHJvdmlkZSB0aWxlcyBhbmQgYWRkaXRpb25hbCBpdGVtczsgCkFwcGx5IGdyb3V0IGluIHNwYWNlcnMgYW5kIHNlYWxlciBvbiB0aWxlcyAoQ29tcGFueSBwcm92aWRlIGdyb3V0IOKAkyBmcm9tIGNvbXBhbnkgc2VsZWN0aW9uKQrCoApEcnl3YWxsICYgUGFpbnQgClByZXBhcmUgYW5kIGluc3RhbGwgZHJ5d2FsbCBhcyBuZWVkZWQgaW4gdGhlIHdvcmtpbmcgYXJlYXM7CkFwcGx5IHByaW1lciBhbmQgcGFpbnQgKFRleHR1cmUsIGNvbG9yIGFuZCBmaW5pc2ggY29hdCAtIFRCRCk7Ckluc3RhbGwgYmFzZWJvYXJkcyBbU2VsZWN0OiBTdGFuZGFyZCBQVkMgLyBNREYgdXAgdG8gNOKAnS8gT3RoZXI6IF9dOwrCoMKgCkluc3RhbGxhdGlvbiBvZiBmaXh0dXJlcyAKU2hvd2VyIGZpeHR1cmVzIFtTaG93ciBoZWFkIGFuZCBoYW5kbGVzXTsKQmF0aHJvb20gZml4dHVyZXM6IFtTZWxlY3Q6IFZhbml0eSAtIHN0b2NrL3ByZWZhYiAoc2luZ2xlL2RvdWJsZSkgLyBtaXJyb3IgLyBTaW5rICYgRmF1Y2V0IC8gVG9pbGV0IC8gQmF0aHJvb20gaGFyZHdhcmUgc2V0IC8gT3RoZXIgOl9dCkFkZGl0aW9uYWwgZml4dHVyZXMsIGhhcmR3YXJlIG9yIGFwcGxpYW5jZXMgdG8gYmUgaW5zdGFsbGVkOiAKQ3VzdG9tZXJzIHNoYWxsIHByb3ZpZGUgYWxsIGl0ZW1zLiAKwqAKwqAKU2hvd2VyIGdsYXNzCkdsYXNzIGRvb3IgcHJvdmlkZWQgYnk6IFtTZWxlY3Q6IEN1c3RvbWVyIC1pbmNsdWRpbmcgaW5zdGFsbGF0aW9uLyBDb21wYW55IC0gZnJvbSBjb21wYW55IHNlbGVjdGlvbiB1bmRlciBhbGxvd2FuY2VdIApJZiBwcm92aWRlZCBieSB0aGUgY29tcGFueSDigJMgZ2xhc3MgZG9vciBkZXRhaWxzOiBbU2VsZWN0OiBHbGFzcyAtIEZyYW1lZCAvIEZyYW1lbGVzcyA7IFNlbGVjdDogSGFyZHdhcmUgLSBDaHJvbWUgLyBCcnVzaGVkIG5pY2tlbCAvIEJyb256ZSAvIEN1c3RvbSA7IFNlbGVjdDogSGFuZGxlIC0gU3F1YXJlIC8gUm91bmQgLyBPdGhlcjogXykKwqAKwqAKVHJhc2ggJiBDbGVhbmluZyAKQ292ZXIgd29ya2luZyBhcmVhczsKSGF1bCBhd2F5IGFsbCBjb25zdHJ1Y3Rpb24gZGVicmlzOwpDb21wbGV0ZSBmaW5hbCBjbGVhbi11cAo="
        )
        val decodedString = String(decodedBytes)

        binding.de.text = decodedString
        binding.mEtSearchName.addWatcher {
            mHomeLeadsAdapter.filter.filter(it)
        }
        binding.simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        clicked = "0"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.getAllLeads("lead","1", "200", "all")
                            }
                        }
                    }
                    1 -> {
                        clicked = "1"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.getPendingLeads("lead","1", "100", "pending")
                            }
                        }
                    }
                    2 -> {
                        clicked = "2"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.getFollowUPLeads("lead","1", "100", "follow_up")
                            }
                        }
                    }
                    3 -> {
                        clicked = "3"
                        mList.clear()
                        mHomeLeadsAdapter.notifyDataSetChanged()
                        if (isInternetConnected(requireActivity())) {
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.getSaleLeads("lead","1", "100", "sale")
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

        binding.mIvAdd.setOnClickListener { startActivity(Intent(activity, AddLeadActivity::class.java)) }
        binding.mIvSettingsLead.setOnClickListener {
//            startActivity(Intent(requireActivity(), SettingsActivity::class.java))
        }
//        binding.tvStatuses.setOnClickListener { showDropDownMenu(tvStatuses, 1) }
        binding.rvLeads.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.rvLeads.adapter = mHomeLeadsAdapter

        if (mPrefs.getKeyValue(PreferenceConstants.USER_NAME).isEmpty()) {
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getProfile()
                }
            }
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
                    mHomeLeadsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    Log.e("MY Error",it.message.toString())
//                    toast(it.message.toString())
                    startActivity(Intent(requireContext(),LoginActivity::class.java))
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
                    mHomeLeadsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseFollowUPLeadsModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.leadsData)
                    mHomeLeadsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responseSaleLeadsModel.observe(requireActivity(), androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.leadsData)
                    mHomeLeadsAdapter.notifyDataSetChanged()
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
                    var string=gson.toJson(it);
                    startActivity(Intent(requireContext(), AddLeadActivity::class.java).putExtra("edit",true).putExtra("lead",string))
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

    override fun onSingleItemClick(item: Any, position: Int) {
        if (item == "2") {
            val lat = mList[position].address.location.coordinates[0]
            val long = mList[position].address.location.coordinates[1]
            val uri = "geo:${lat},${long}?q=${lat},${long}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)

        }
        else if (item == "3") {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("smsto:" + "+1 " + mList[position].client[0].phone_no)
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }
        else if (item == "4") {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "+1 " + mList[position].client[0].phone_no)
            startActivity(dialIntent)
        }
        else {
            requireActivity().startActivity(
                Intent(
                    requireActivity(), LeadDetailActivity::class.java
                ).putExtra("id", mList[position]._id)
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
//                presenter.junkleads(mList[selectedPosition]._id)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.LeadDelete(mList[selectedPosition]._id)
                }
            }
            else if (it.itemId == R.id.item_edit) {
                if (isInternetConnected(requireActivity())) {
//                    presenter.getLeadDetail(mList[selectedPosition]._id)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getLeadDetail(mList[selectedPosition]._id)
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    private fun shareData(selectedPosition: Int){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
        var shareMessage = """
                    ${
            "\n" + "Lead Detail" + "\n" + mList.get(selectedPosition).project_name + "\n" +
                    mList.get(selectedPosition).client.get(0).name + "\n" +
                    mList.get(selectedPosition).client.get(0).email + "\n" +
                    mList.get(selectedPosition).client.get(0).phone_no + "\n" +
                    mList.get(selectedPosition).address.street+ "\n"
//                         "Job Detail"+"\n"+ "Project Name - "+etTvTitle.getText().toString()+"\n"+
//                            "Client Name - "+etTvName.getText().toString()+"\n"+
//                            "Client Email - "+etTvEmail.getText().toString()+"\n"+
//                            "Client Phone - "+etTvPhone.getText().toString()+"\n"+
//                            "Client Address - "+address+"\n"
        }
              
                    """.trimIndent()

        shareMessage = """ $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}""".trimIndent()
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "choose one"))

    }

    override fun onResume() {
        super.onResume()
        binding.mEtSearchName.setText("")
        if (clicked == "0") {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getAllLeads("lead","1", "100", "all")
                }
            }
        } else if (clicked == "1") {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getPendingLeads("lead","1", "100", "pending")
                }
            }
        } else if (clicked == "2") {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getFollowUPLeads("lead","1", "100", "follow_up")
                }
            }
        } else if (clicked == "3") {
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getSaleLeads("lead","1", "100", "sale")
                }
            }
        }
        else {
            mList.clear()
            mHomeLeadsAdapter.notifyDataSetChanged()
            if (isInternetConnected(requireActivity())) {
                Constants.showLoading(requireActivity())
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getAllLeads("lead","1", "200", "all")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clicked = "0"
    }

}