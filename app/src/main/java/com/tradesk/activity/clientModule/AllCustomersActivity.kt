package com.tradesk.activity.clientModule

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.Client
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.adapter.CustomersAdapter
import com.tradesk.databinding.ActivityAllCustomersBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class AllCustomersActivity : AppCompatActivity(), SingleListCLickListener, LongClickListener {
    companion object{
        lateinit var context: AllCustomersActivity
    }
    lateinit var binding:ActivityAllCustomersBinding
    lateinit var viewModel:CustomerViewModel
    val mList = mutableListOf<Client>()
    val mCustomersAdapter by lazy { CustomersAdapter(this, mList, mList, this,this) }
    var selectedIdArray = ArrayList<String>()
    var itemPosition: Int? = null
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_all_customers)
        viewModel=ViewModelProvider(this).get(CustomerViewModel::class.java)
        context =this
        binding.rvCustomers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvCustomers.adapter = mCustomersAdapter

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllClient("client","1", "50")
            }
        }

        binding.mEtSearch.addWatcher {
            mCustomersAdapter.filter.filter(it)
        }

        binding.mIvBack.setOnClickListener { finish() }

        binding.mIvRightMenuCustomer.setOnClickListener {
            if (mList.isNotEmpty()){
//                showRightMenu(it)
            }
        }
        binding.mIvAdd.setOnClickListener {
            val intent = Intent(this, AddClientActivity::class.java)
            intent.putExtra("screen_title", "Add Client")
            startActivity(intent)
        }
        initObserve()
    }
    fun initObserve() {
        viewModel.responsAllClient.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mList.addAll(it.data!!.data.client)
                    mCustomersAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responsDeleteSelectedClient.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted Successfully")
                    mList.removeAt(itemPosition!!)
                    mCustomersAdapter.notifyItemRemoved(itemPosition!!)
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


    override fun onSingleListClick(item: Any, position: Int) {
        if (intent.hasExtra("from"))
        {
            val builder = GsonBuilder()
            val gson = builder.create()
            val returnIntent = Intent()
            returnIntent.putExtra("result", mList[position]._id)
            returnIntent.putExtra("name", mList[position].name)
            returnIntent.putExtra("email", mList[position].email)
            returnIntent.putExtra("image", mList[position].image)
            returnIntent.putExtra("client", gson.toJson(mList[position]))
            returnIntent.putExtra(
                "address",
                mList[position].address.street + ", " + mList[position].address.city + ", " + mList[position].address.state + ", " + mList[position].address.zipcode
            )
            returnIntent.putExtra("phonenumber", mList[position].phone_no)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
        //My Code
        else if (item.equals("main")) {
            startActivity(
                Intent(this, CustomerDetailActivity::class.java)
                    .putExtra("from","CustomerActivity")
                    .putExtra("title","User Profile")
                    .putExtra("id", mList[position]._id)
                    .putExtra("type", mList[position].type)
                    .putExtra("name", mList[position].name)
                    .putExtra("email", mList[position].email)
                    .putExtra("phone", mList[position].phone_no)
                    .putExtra("trade", mList[position].trade)
                    .putExtra(
                        "address", if (mList[position].address.street.isNotEmpty()) {
                            mList[position].address.street + ", " + mList[position].address.city + ", " + mList[position].address.state + ", " + mList[position].address.zipcode
                        } else {
                            mList[position].address.street + ", " + mList[position].address.city + ", " + mList[position].address.state + ", " + mList[position].address.zipcode
                        }
                    )
                    .putExtra("image", mList[position].image)
                    .putExtra("trade", mList[position].trade)

            )
        }
    }

    override fun onLongClickListener(item: Any, position: Int) {
        showRightMenuLongClick(item as View,position)
    }

    private fun showRightMenuLongClick(anchor: View,selectedPosition:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_download).isVisible = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

        popup.setOnDismissListener {
            if (selectedPosition != null) {
                selectedIdArray.removeAll(setOf(mList[selectedPosition]._id))
            }
        }

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (selectedPosition != null) {
                    shareData(selectedPosition)
                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_delete) {
                if (selectedPosition != null) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this) && selectedPosition != null) {
                                itemPosition = selectedPosition
                                selectedIdArray.add(mList[selectedPosition]._id)
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedClient(selectedIds)
                                }
                            }
                        })
                }else{
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_edit) {
                if (selectedPosition!=null) {
                    startActivity(
                        Intent(this, AddClientActivity::class.java)
                            .putExtra("title", "Edit Client")
                            .putExtra("id", mList.get(selectedPosition)._id)
                            .putExtra("type", mList.get(selectedPosition).type)
                    )
                    //  finish()
                }else{
                    toast("Select an item")
                }
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun shareData(selectedPosition: Int){
        if (selectedPosition!=null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
            var shareMessage = """
                    ${
                "\n" + mList[selectedPosition].name + "\n" +
                        mList[selectedPosition].email + "\n" +
                        mList[selectedPosition].phone_no + "\n"
            }        
               """.trimIndent()
            shareMessage = """
                    $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}
                    """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        }
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllClient("client","1", "50")
            }
        }
    }


}