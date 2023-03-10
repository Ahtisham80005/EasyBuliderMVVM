package com.tradesk.activity.proposalModule

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.Proposal
import com.tradesk.Model.ProposalDetailModel
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.adapter.ProposalsAdapter
import com.tradesk.databinding.ActivityProposalsBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ProposalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ProposalsActivity : AppCompatActivity(), SingleListCLickListener,
    LongClickListener {
    val mList = mutableListOf<Proposal>()
    val mProposalsAdapter by lazy { ProposalsAdapter(this, mList, mList, this,this) }
    var selected_position = 0
    var clicked = "0"
    var proposal_count = ""
    var mTitle = ""
    lateinit var proposalDetail: ProposalDetailModel
    var TAG = "ProposalsActivity"
    var selectedImageArray = ArrayList<String>()
    var selectedIdArray = ArrayList<String>()
    var itemPosition: Int? = null
    lateinit var binding:ActivityProposalsBinding
    lateinit var viewModel:ProposalsViewModel
    companion object {
        lateinit var context: ProposalsActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_proposals)
        viewModel= ViewModelProvider(this).get(ProposalsViewModel::class.java)
        context = this

        binding.mIvRightMenuProposal.visibility = View.VISIBLE

        binding.textView6.setText(intent.getStringExtra("title"))
        mTitle = intent.getStringExtra("title").toString();
        if(mTitle.equals("proposals",true)){
            binding.textView6.setText("Estimates")
        }
        val firstTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
        firstTab.text = "Pending"
        binding.simpleTabLayout.addTab(firstTab)

        if (!mTitle.equals("Invoices", true)) {
            val thirdTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
            thirdTab.text = "Signed"
            binding.simpleTabLayout.addTab(thirdTab)
        } else {
            val thirdTab: TabLayout.Tab = binding.simpleTabLayout.newTab()
            thirdTab.text = "Paid"
            binding.simpleTabLayout.addTab(thirdTab)
        }
        binding.mEtSearchName.visibility = View.VISIBLE
        binding.mEtSearchName.addWatcher {
            mProposalsAdapter.filter.filter(it)
        }
        binding.mIvAddProposal.visibility=View.VISIBLE
        binding.simpleTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        clicked = "0"
                        binding.mIvAddProposal.visibility=View.VISIBLE
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            if (isInternetConnected(this@ProposalsActivity)) {
                                Constants.showLoading(this@ProposalsActivity)
                                if (intent.hasExtra("job_id")) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals(
                                            "1",
                                            "30",
                                            "pending",
                                            "proposal",
                                            intent.getStringExtra("job_id").toString()
                                        )
                                    }

                                } else {
                                    Constants.showLoading(this@ProposalsActivity)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals("1", "30", "pending", "proposal", "")
                                    }
                                }

                            }
                        } else {
                            if (isInternetConnected(this@ProposalsActivity)) {
                                if (intent.hasExtra("job_id")) {
                                    Constants.showLoading(this@ProposalsActivity)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals(
                                            "1",
                                            "30",
                                            "pending",
                                            "invoice",
                                            intent.getStringExtra("job_id").toString()
                                        )
                                    }

                                } else {
                                    Constants.showLoading(this@ProposalsActivity)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals("1", "30", "pending", "invoice", "")
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        clicked = "1"
                        binding.mIvAddProposal.visibility=View.GONE
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            if (isInternetConnected(this@ProposalsActivity)) {
                                //Inprocess
                                if (intent.hasExtra("job_id")) {
                                    Constants.showLoading(this@ProposalsActivity)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals(
                                            "1",
                                            "30",
                                            "completed",
                                            "proposal",
                                            intent.getStringExtra("job_id").toString()
                                        )
                                    }

                                } else {
                                    Constants.showLoading(this@ProposalsActivity)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals("1", "30", "completed", "proposal", "")
                                    }
                                }

                            }
                        } else {
                            if (isInternetConnected(this@ProposalsActivity)) {
                                if (intent.hasExtra("job_id")) {
                                    Constants.showLoading(this@ProposalsActivity)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals(
                                            "1",
                                            "30",
                                            "completed",
                                            "invoice",
                                            intent.getStringExtra("job_id").toString()
                                        )
                                    }
                                } else {
                                    Constants.showLoading(this@ProposalsActivity)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.getProposals("1", "30", "completed", "invoice", "")
                                    }
                                }
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

        binding.rvProposals.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvProposals.adapter = mProposalsAdapter

        binding.mIvBack.setOnClickListener { finish() }

        binding.mIvRightMenuProposal.setOnClickListener {
            if (mList.isNotEmpty()){
                showRightMenu(it)
            }
        }
        binding.mIvAddProposal.setOnClickListener {

            if (mList.isNotEmpty())
            {
                var get_proposal_count = mList.size + 1
                if (get_proposal_count.toString().length == 1) {
                    proposal_count = "0000" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 1) {
                    proposal_count = "000" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 2) {
                    proposal_count = "00" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 3) {
                    proposal_count = "0" + get_proposal_count.toString()
                } else if (get_proposal_count.toString().length == 4) {
                    proposal_count = get_proposal_count.toString()
                }
            } else {
                proposal_count = "00001"
            }

            if (intent.getStringExtra("title").equals("Proposals")) {
                if (intent.hasExtra("job_id")) {
                    startActivity(
                        Intent(this, AddProposalActivity::class.java)
                            .putExtra("job_id", intent.getStringExtra("job_id"))
                            .putExtra("client_name", intent.getStringExtra("client_name"))
                            .putExtra("client_id", intent.getStringExtra("client_id"))
                            .putExtra("client_email", intent.getStringExtra("client_email"))
                            .putExtra("is_proposal", true)
                            .putExtra("count", proposal_count)
                    )
                } else {
                    startActivity(
                        Intent(this, AddProposalActivity::class.java).putExtra(
                            "count",
                            proposal_count
                        )
                    )
                }
            } else {
                if (intent.hasExtra("job_id")) {
                    startActivity(
                        Intent(this, InvoicesActivity::class.java)
                            .putExtra("job_id", intent.getStringExtra("job_id"))
                            .putExtra("client_name", intent.getStringExtra("client_name"))
                            .putExtra("client_id", intent.getStringExtra("client_id"))
                            .putExtra("client_email", intent.getStringExtra("client_email"))
                            .putExtra("count", proposal_count)
                    )
                } else {
                    startActivity(
                        Intent(this, InvoicesActivity::class.java).putExtra(
                            "count",
                            proposal_count
                        )
                    )
                }
            }
        }
        initObserve()
    }

    fun initObserve()
    {
        viewModel.responsePorposalsListModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data!!.data.proposal_list.isNotEmpty()) {
                        mList.clear()
                        mList.addAll(it.data.data.proposal_list)
                        mProposalsAdapter.notifyDataSetChanged()
                    } else {
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            toast("You have no proposal added yet.")
                            mList.clear()
                            mProposalsAdapter.notifyDataSetChanged()
                        } else {
                            toast("You have no invoice added yet.")
                            mList.clear()
                            mProposalsAdapter.notifyDataSetChanged()
                        }

                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseProposalDetailModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    setPorposalsDetails(it.data!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseDeleteSelectedProposalSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted Successfully")
                    mList.removeAt(itemPosition!!)
                    mProposalsAdapter.notifyItemRemoved(itemPosition!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseDeleteAllProposalSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted Successfully")
                    mList.clear()
                    mProposalsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseDeleteProposalSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted Successfully")
                    mList.removeAt(itemPosition!!)
                    mProposalsAdapter.notifyItemRemoved(itemPosition!!)
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
    }

    fun setPorposalsDetails(it: ProposalDetailModel) {
        proposalDetail = it
        val builder = GsonBuilder()
        val gson = builder.create()
        var string = gson.toJson(it);
        if (intent.getStringExtra("title").equals("Proposals")) {
            if (intent.hasExtra("job_id")) {
                startActivity(
                    Intent(this, AddProposalActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("job_id"))
                        .putExtra("client_name", intent.getStringExtra("client_name"))
                        .putExtra("client_id", intent.getStringExtra("client_id"))
                        .putExtra("client_email", intent.getStringExtra("client_email"))
                        .putExtra("is_proposal", true)
                        .putExtra("is_EditMode", true)
                        .putExtra("proposalData", string)
                        .putExtra("count", proposal_count)
                )
            }
            else {
                startActivity(Intent(this, AddProposalActivity::class.java).putExtra("count", proposal_count
                    ).putExtra("proposalData", string).putExtra("is_EditMode", true)
                )
            }
        } else {
            if (intent.hasExtra("job_id")) {
                startActivity(
                    Intent(this, InvoicesActivity::class.java)
                        .putExtra("job_id", intent.getStringExtra("job_id"))
                        .putExtra("client_name", intent.getStringExtra("client_name"))
                        .putExtra("client_id", intent.getStringExtra("client_id"))
                        .putExtra("client_email", intent.getStringExtra("client_email"))
                        .putExtra("count", proposal_count)
                        .putExtra("proposalData", string)
                        .putExtra("is_EditMode", true)
                )
            } else {
                startActivity(
                    Intent(this, InvoicesActivity::class.java).putExtra(
                        "count",
                        proposal_count
                    ).putExtra("proposalData", string).putExtra("is_EditMode", true)
                )
            }
        }

    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Delete")) {
            AllinOneDialog(ttle = "Delete",
                msg = "Are you sure you want to Delete it ?",
                onLeftClick = {/*btn No click*/ },
                onRightClick = {/*btn Yes click*/
                    selected_position = position
                    if (isInternetConnected(this)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteproposal(mList.get(position)._id)
                        }
                    }
                })
        }
        else if (item.equals("edit")) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getDetail(mList.get(position)._id)
            }

        }
        else if (item.equals("Pdf")){
            if (mList[position].invoice_url.isNotEmpty()) {

                var intent=Intent(this@ProposalsActivity,PDFViewNewActivity::class.java)
                    .putExtra("pdfurl", mList[position].invoice_url)
                    .putExtra("title", intent.getStringExtra("title"))
                    .putExtra("status", mList[position].status)
                    .putExtra("id", mList[position]._id)

                if( mList[position].client_id!=null)
                {
                    intent.putExtra("email", mList[position].client_id.email)
                }
                else
                {
                    intent.putExtra("email"," ")
                }

                startActivity(intent)
            }
        }
    }

    fun showRightMenu(anchor: View ): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.sub_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{

            if (it.itemId == R.id.item_select_items){
                if (clicked.equals("0") && intent.getStringExtra("title").equals("Proposals")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "0").putExtra("title","Proposals")
                        .putExtra("selectType","single").putExtra("job_id",intent.getStringExtra("job_id")))
                }else if (clicked.equals("1") && intent.getStringExtra("title").equals("Proposals")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "1").putExtra("title","Proposals")
                        .putExtra("selectType","single").putExtra("job_id",intent.getStringExtra("job_id")))
                }else if (clicked.equals("0") && intent.getStringExtra("title").equals("Invoices")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "0").putExtra("title","Invoices")
                        .putExtra("selectType","single").putExtra("job_id",intent.getStringExtra("job_id")))
                }else if (clicked.equals("1") && intent.getStringExtra("title").equals("Invoices")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "1").putExtra("title","Invoices")
                        .putExtra("selectType","single").putExtra("job_id",intent.getStringExtra("job_id")))
                }

            }else if (it.itemId == R.id.item_select_all){
                if (clicked.equals("0") && intent.getStringExtra("title").equals("Proposals")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "0").putExtra("title","Proposals")
                        .putExtra("selectType","all").putExtra("job_id",intent.getStringExtra("job_id")))
                }else if (clicked.equals("1") && intent.getStringExtra("title").equals("Proposals")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "1").putExtra("title","Proposals")
                        .putExtra("selectType","all").putExtra("job_id",intent.getStringExtra("job_id")))
                }else if (clicked.equals("0") && intent.getStringExtra("title").equals("Invoices")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "0").putExtra("title","Invoices")
                        .putExtra("selectType","all").putExtra("job_id",intent.getStringExtra("job_id")))
                }else if (clicked.equals("1") && intent.getStringExtra("title").equals("Invoices")){
                    startActivity(Intent(this, SelectItemPropsalActivity::class.java)
                        .putExtra("clicked", "1").putExtra("title","Invoices")
                        .putExtra("selectType","all").putExtra("job_id",intent.getStringExtra("job_id")))
                }
            }else if(it.itemId == R.id.item_delete_all){

                AllinOneDialog(ttle = "Delete",
                    msg = "Are you sure you want to Delete all ?",
                    onLeftClick = {/*btn No click*/ },
                    onRightClick = {/*btn Yes click*/
                        if (isInternetConnected(this)) {
                            if (clicked.equals("0")) {
                                if (intent.getStringExtra("title").equals("Proposals")) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.deleteAllProposal("proposal", "pending")
                                    }
                                }else{
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.deleteAllProposal("invoice", "pending")
                                    }
                                }
                            }

                            if (clicked.equals("1")) {
                                if (intent.getStringExtra("title").equals("Proposals")) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.deleteAllProposal("proposal", "completed")
                                    }
                                }else{
                                    CoroutineScope(Dispatchers.IO).launch {
                                        viewModel.deleteAllProposal("invoice", "completed")
                                    }

                                }
                            }
                        }
                    })
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    override fun onLongClickListener(item: Any, position: Int) {
        showRightMenuLongClick(item as View,position)
    }
    private fun showRightMenuLongClick(anchor: View,selectedPosition:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }
        popup.setOnDismissListener {
            if (selectedPosition != null) {
                selectedIdArray.removeAll(setOf(mList[selectedPosition]._id))
                selectedImageArray.removeAll(setOf(mList[selectedPosition].invoice_url))
            }
        }
        if (intent.getStringExtra("title").equals("Invoices")) {
            popup.menu.findItem(R.id.item_edit).isVisible = false
        }
        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share) {
                if (selectedPosition!=null) {
                    shareImage(selectedPosition)
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_delete) {
                if (selectedPosition != null) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this) && selectedPosition != null) {
                                itemPosition = selectedPosition
                                selectedIdArray.add(mList[selectedPosition]._id)
                                val selectedIds = SelectedIds(selectedIdArray)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedProposal(selectedIds)
                                }
                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                }else{
                    toast("Select an item")
                }

            } else if (it.itemId == R.id.item_download) {
                if (selectedPosition !=null) {
                    selectedIdArray.add(mList[selectedPosition]._id)
                    selectedImageArray.add(mList[selectedPosition].invoice_url)
                    selectedIdArray.zip(selectedImageArray).forEach { pair ->
                        downloadFile(pair.first, pair.second) }
                } else {
                    toast("Select an item")
                }

            } else if (it.itemId == R.id.item_edit) {
                if (intent.getStringExtra("title").equals("Proposals")) {
                    if (isInternetConnected(this) && selectedPosition != null) {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getDetail(mList[selectedPosition]._id)
                        }
                        Log.d(TAG, "showRightMenu: " + mList[selectedPosition]._id)
                    } else {
                        toast("Select an item")
                    }
                }
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun shareImage(selectedPosition:Int) {
        if (selectedPosition != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, mList[selectedPosition!!].invoice_url)
            startActivity(Intent.createChooser(shareIntent, "Share link using"))
        }
    }
    private fun downloadFile(filename: String, downloadUrlOfImage: String) {
        try {
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("pdf") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOCUMENTS,
                    File.separator.toString() + filename + ".pdf"
                )
            dm.enqueue(request)
            Toast.makeText(this, "File download started.", Toast.LENGTH_SHORT).show()
            // finish()
        } catch (e: Exception) {
            toast("File download failed.")
        }
    }

    override fun onResume() {
        super.onResume()
        if (clicked.equals("0")) {
            if (intent.getStringExtra("title").equals("Proposals")) {
                if (isInternetConnected(this)) {
                    if (intent.hasExtra("job_id")) {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals("1", "30", "pending", "proposal", intent.getStringExtra("job_id").toString())
                        }
                    } else {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals("1", "30", "pending", "proposal", "")
                        }
                    }

                }
            } else {
                if (isInternetConnected(this)) {
                    if (intent.hasExtra("job_id")) {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals("1", "30", "pending", "invoice", intent.getStringExtra("job_id").toString())
                        }
                    } else {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals("1", "30", "pending", "invoice", "")
                        }
                    }
                }
            }
        }
        else if (clicked.equals("1")) {
            if (intent.getStringExtra("title").equals("Proposals")) {
                if (isInternetConnected(this)) {
                    if (intent.hasExtra("job_id")) {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals("1", "30", "completed", "proposal", intent.getStringExtra("job_id").toString())
                        }
                    } else {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals("1", "30", "completed", "proposal", "")
                        }
                    }

                }
            } else {
                if (isInternetConnected(this)) {
                    if (intent.hasExtra("job_id")) {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals(
                                "1",
                                "30",
                                "completed",
                                "invoice",
                                intent.getStringExtra("job_id").toString()
                            )
                        }
                    } else {
                        Constants.showLoading(this@ProposalsActivity)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getProposals("1", "30", "completed", "invoice", "")
                        }
                    }
                }
            }
        }

//        else if (clicked.equals("2")) {
//            if (intent.getStringExtra("title").equals("Proposals")) {
//                if (isInternetConnected()) {
//                    if (intent.hasExtra("job_id")) {
//                        presenter.getProposals(
//                            "1",
//                            "30",
//                            "completed",
//                            "proposal",
//                            intent.getStringExtra("job_id").toString()
//                        )
//                    } else {
//                        presenter.getProposals("1", "30", "completed", "proposal", "")
//                    }
//
//                }
//            } else {
//                if (isInternetConnected()) {
//                    if (intent.hasExtra("job_id")) {
//                        presenter.getProposals(
//                            "1",
//                            "30",
//                            "completed",
//                            "invoice",
//                            intent.getStringExtra("job_id").toString()
//                        )
//                    } else {
//                        presenter.getProposals("1", "30", "completed", "invoice", "")
//                    }
//                }
//            }
//        }

    }
}