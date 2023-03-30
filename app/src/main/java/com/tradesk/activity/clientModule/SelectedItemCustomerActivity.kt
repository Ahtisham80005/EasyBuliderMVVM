package com.tradesk.activity.clientModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Client
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.databinding.ActivitySelectedItemCustomerBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class SelectedItemCustomerActivity : AppCompatActivity(), SingleListCLickListener,
    UnselectCheckBoxListener {
    val mList = mutableListOf<Client>()
    lateinit var mSelectedCustomersAdapter: SelectedItemCustomersAdapter
    var selectType = ""
    var selectedPosition: Int? = null
    var unSelectedPosition: Int? = null
    var increment = 0
    var decrement = 0
    var selectionResult = 0
    var selectedIdArray = ArrayList<String>()
    var TAG = "SelectedItemCustomerActivity"
    lateinit var binding:ActivitySelectedItemCustomerBinding
    lateinit var viewModel: CustomerViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_selected_item_customer)
        viewModel= ViewModelProvider(this).get(CustomerViewModel::class.java)
        setData()
    }

    private fun setData(){

        selectType = intent.getStringExtra("selectType").toString()
        mSelectedCustomersAdapter = SelectedItemCustomersAdapter(this,selectType,mList,mList,
            this,this)
        binding.rvSelectedCustomers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSelectedCustomers.adapter = mSelectedCustomersAdapter

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllClient("client","1", "50")
            }

        }
        binding.mIvBack.setOnClickListener { finish() }

        binding.mIvRightMenuSelectCustomer.setOnClickListener {
            showRightMenu(it)
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
                    mSelectedCustomersAdapter.notifyDataSetChanged()
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
                    toast("Deleted successfully")
                    mList.clear()
                    mSelectedCustomersAdapter.notifyDataSetChanged()
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
        viewModel.responsDeleteAllClient.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted successfully")
                    mList.clear()
                    mSelectedCustomersAdapter.notifyDataSetChanged()
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

    }

    private fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())

        popup.menu.findItem(R.id.item_download).isVisible = false

        selectionResult = increment - decrement

        Log.d(TAG, "showRightMenu: " + selectionResult.toString())
        Log.d(TAG, "selectedposition: " + selectedPosition.toString())
        Log.d(TAG, "unselectedposition: " + unSelectedPosition.toString())

        if (selectionResult > 1) {
            popup.menu.findItem(R.id.item_share).isVisible = false
            popup.menu.findItem(R.id.item_edit).isVisible = false
        }

        if (selectType.equals("all")) {
            popup.menu.findItem(R.id.item_share).isVisible = false

        }

        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share) {
                if (selectionResult >= 1) {
                    shareData()
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
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedClient(selectedIds)
                                }
                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                } else if (isInternetConnected(this) && selectType.equals("all")) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete all ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            Constants.showLoading(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllClient("client")
                            }
                        })
                }else{
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_edit) {
                if (selectedPosition!=null && selectionResult >= 1) {
                    startActivity(
                        Intent(this, AddClientActivity::class.java)
                            .putExtra("title", "Edit Client")
                            .putExtra("id", mList.get(selectedPosition!!)._id)
                            .putExtra("type", mList.get(selectedPosition!!).type)
                    )
                    finish()
                }else{
                    toast("Select an item")
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun shareData(){
        if (selectedPosition!=null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")

            var phone = Constants.insertString(mList[selectedPosition!!].phone_no, "", 0)
            phone = Constants.insertString(phone!!, ")", 2)
            phone = Constants.insertString(phone!!, " ", 3)
            phone = Constants.insertString(phone!!, "-", 7)
            phone = "(" + phone!!

            var shareMessage = """
                    ${
                "\n" + mList[selectedPosition!!].name + "\n" +
                        mList[selectedPosition!!].email + "\n" +
                        phone + "\n"

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
        mSelectedCustomersAdapter.notifyDataSetChanged()
    }

    override fun onSingleListClick(item: Any, position: Int) {
        selectedPosition = position
        selectedIdArray.add(mList[position]._id)
        increment = item as Int
        Log.d(TAG, "onSingleListClick: " + increment.toString())

        if (intent.hasExtra("from")) {
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

    }


    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        unSelectedPosition = position
        selectedIdArray.removeAll(setOf(mList[position]._id))
        decrement = item as Int
        Log.d(TAG, "onCheckBoxUnCheckClick: " + decrement.toString())
    }
}