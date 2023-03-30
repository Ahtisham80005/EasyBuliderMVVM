package com.tradesk.activity.proposalModule

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Proposal
import com.tradesk.Model.ProposalDetailModel
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.activity.proposalModule.adapter.SelectItemProposalsAdapter
import com.tradesk.databinding.ActivitySelectItemPropsalBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ProposalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
@AndroidEntryPoint
class SelectItemPropsalActivity : AppCompatActivity() , SingleListCLickListener,
    UnselectCheckBoxListener {

    val mList = mutableListOf<Proposal>()
    lateinit var mProposalsAdapter: SelectItemProposalsAdapter
    var selectedPosition: Int? = null
    var unSelectedPosition: Int? = null
    var proposal_count = ""
    lateinit var proposalDetail: ProposalDetailModel
    var selectType = ""
    var selectedImageArray = ArrayList<String>()
    var selectedIdArray = ArrayList<String>()
    var selectedPositionArray = ArrayList<Int>()
    var unSelectedPositionArray = ArrayList<Int>()
    var increment = 0
    var decrement = 0
    var selectionResult = 0
    var TAG = "SelectItemPropsalActivity"
    lateinit var binding:ActivitySelectItemPropsalBinding
    lateinit var viewModel: ProposalsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_select_item_propsal)
        viewModel= ViewModelProvider(this).get(ProposalsViewModel::class.java)
        setData()
        clickEvent()
    }

    private fun setData() {
        selectType = intent.getStringExtra("selectType").toString()

        mProposalsAdapter =
            SelectItemProposalsAdapter(this, selectType, mList, mList, this, this)

        binding.rvSelectItemProposal.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectItemProposal.adapter = mProposalsAdapter

        if (intent.getStringExtra("clicked").equals("0")
            && intent.getStringExtra("title").equals("Proposals")
        ) {
            if (isInternetConnected(this)) {
                binding.tvStatus.text = "Pending"
                if (intent.getStringExtra("job_id") !=null) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals(
                            "1", "30", "pending", "proposal", intent.getStringExtra("job_id").toString())
                    }

                } else {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals("1", "30", "pending", "proposal", "")
                    }
                }
            }
        } else if (intent.getStringExtra("clicked").equals("1")
            && intent.getStringExtra("title").equals("Proposals")
        ) {
            if (isInternetConnected(this)) {
                binding.tvStatus.text = "Completed"
                if (intent.getStringExtra("job_id") !=null) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals(
                            "1",
                            "30",
                            "completed",
                            "proposal",
                            intent.getStringExtra("job_id").toString())
                    }

                } else {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals("1", "30", "completed", "proposal", "")
                    }

                }
            }
        } else if (intent.getStringExtra("clicked").equals("0")
            && intent.getStringExtra("title").equals("Invoices")
        ) {
            if (isInternetConnected(this)) {
                binding.tvStatus.text = "Pending"
                if (intent.getStringExtra("job_id") !=null) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals(
                            "1", "30", "pending", "invoice", intent.getStringExtra("job_id").toString()
                        )
                    }
                } else {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals("1", "30", "pending", "invoice", "")
                    }
                }
            }
        } else if (intent.getStringExtra("clicked").equals("1")
            && intent.getStringExtra("title").equals("Invoices")
        ) {
            if (isInternetConnected(this)) {
                binding.tvStatus.text = "Paid"
                if (intent.getStringExtra("job_id") !=null) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals(
                            "1", "30", "completed", "invoice",
                            intent.getStringExtra("job_id").toString()
                        )
                    }

                } else {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.getProposals("1", "30", "completed", "invoice", "")
                    }

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
                        mList.addAll(it.data!!.data.proposal_list)
                        mProposalsAdapter.notifyDataSetChanged()
                    } else {
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            toast("You have no proposal added yet.")
                        } else {
                            toast("You have no invoice added yet.")
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
        viewModel.responseDeleteAllProposalSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted Successfully")
                    mList.clear()
                    mProposalsAdapter.notifyDataSetChanged()
                    finish()
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
                    if (selectedPosition != null) {
                        toast("Deleted Successfully")
                        //  mList.removeAt(selectedPosition!!)
                        mList.clear()
                        mProposalsAdapter.notifyDataSetChanged()
                        //mProposalsAdapter.notifyItemRemoved(selectedPosition!!)
                        finish()
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
    }

    fun setPorposalsDetails(it: ProposalDetailModel) {
        proposalDetail = it
        val builder = GsonBuilder()
        val gson = builder.create()
        var string = gson.toJson(it);
        if (intent.getStringExtra("title").equals("Proposals")) {
            if (intent.getStringExtra("job_id") !=null) {
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
            } else {
                startActivity(
                    Intent(this, AddProposalActivity::class.java).putExtra(
                        "count",
                        proposal_count
                    ).putExtra("proposalData", string).putExtra("is_EditMode", true)
                )
            }
        } else {
            if (intent.getStringExtra("job_id") !=null) {
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

        finish()

    }


    private fun clickEvent() {
        binding.mIvRightMenuSelectItem.setOnClickListener {
            showRightMenu(it)
        }
        binding.mIvBack.setOnClickListener { finish() }
    }

    private fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())

        selectionResult = increment - decrement
        Log.d(TAG, "showRightMenu: " + selectionResult.toString())
        Log.d(TAG, "selectedposition: " + selectedPosition.toString())
        Log.d(TAG, "unselectedposition: " + unSelectedPosition.toString())

        if (selectionResult > 1) {
            popup.menu.findItem(R.id.item_share).isVisible = false
            popup.menu.findItem(R.id.item_edit).isVisible = false
        }

        if (intent.getStringExtra("title").equals("Invoices")) {
            popup.menu.findItem(R.id.item_edit).isVisible = false
        }

        if (selectType.equals("all")) {
            popup.menu.findItem(R.id.item_share).isVisible = false
            popup.menu.findItem(R.id.item_edit).isVisible = false
            popup.menu.findItem(R.id.item_delete).isVisible = true
        }

        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share) {
                if (selectionResult >= 1) {
                    shareImage()
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_delete) {
                if (selectedPosition != null && selectionResult >= 1 && selectType.equals("single")) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this) && selectedPosition != null && selectionResult >= 1) {
                                //presenter.deleteproposal(mList[selectedPosition!!]._id)
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedProposal(selectedIds)
                                }
                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                } else if (selectType.equals("all") && selectionResult >= 1) {
                    deleteAll()
                }else{
                    toast("Select an item")
                }

            } else if (it.itemId == R.id.item_download) {
                if (selectionResult >= 1) {
                    if (selectType.equals("all")) {
                        mList.forEach { pair ->
                            downloadFile(pair._id, pair.invoice_url)
                        }
                    } else {
                        selectedIdArray.zip(selectedImageArray).forEach { pair ->
                            downloadFile(pair.first, pair.second)
                        }
                    }
                } else {
                    toast("Select an item")
                }
                //  downloadFile(mList[selectedPosition]._id,mList[selectedPosition].invoice_url)

            } else if (it.itemId == R.id.item_edit) {
                if (intent.getStringExtra("title").equals("Proposals")) {
                    if (isInternetConnected(this) && selectedPosition != null && selectionResult >= 1) {
                        Constants.showLoading(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getDetail(mList[selectedPosition!!]._id)
                        }
                        Log.d(TAG, "showRightMenu: " + mList[selectedPosition!!]._id)
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


    private fun shareImage() {
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
            finish()
        } catch (e: Exception) {
            toast("File download failed.")
        }
    }


    private fun deleteAll(){
        AllinOneDialog(ttle = "Delete",
            msg = "Are you sure you want to Delete all ?",
            onLeftClick = {/*btn No click*/ },
            onRightClick = {/*btn Yes click*/
                if (isInternetConnected(this)) {
                    if (intent.getStringExtra("clicked").equals("0")) {
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllProposal("proposal", "pending")
                            }
                        }else{
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllProposal("invoice", "pending")
                            }
                        }
                    }

                    if (intent.getStringExtra("clicked").equals("1")) {
                        if (intent.getStringExtra("title").equals("Proposals")) {
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllProposal("proposal", "completed")
                            }
                        }else{
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllProposal("invoice", "completed")
                            }
                        }
                    }
                }
            })
    }

    override fun onSingleListClick(item: Any, position: Int) {
        selectedPosition = position
        selectedPositionArray.add(position)
        selectedIdArray.add(mList[position]._id)
        selectedImageArray.add(mList[position].invoice_url)
        increment = item as Int
        Log.d(TAG, "onSingleListClick: " + increment.toString())
    }

    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        unSelectedPosition = position
        unSelectedPositionArray.add(position)
        selectedIdArray.removeAll(setOf(mList[position]._id))
        selectedImageArray.removeAll(setOf(mList[position].invoice_url))
        decrement = item as Int
        Log.d(TAG, "onCheckBoxUnCheckClick: " + decrement.toString())
    }
}