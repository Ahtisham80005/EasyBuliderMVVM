package com.tradesk.activity.salesPerson

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Client
import com.tradesk.Model.DataTradesOld
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.activity.clientModule.CustomerDetailActivity
import com.tradesk.adapter.UsersContractSelectedItemAdapter
import com.tradesk.databinding.ActivityUserContractSelectedItemBinding
import com.tradesk.databinding.ActivityUsersContrBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.SalesPersonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UserContractSelectedItemActivity : AppCompatActivity() , SingleListCLickListener,
    UnselectCheckBoxListener {
    var tab_click = "All"
    var mList = mutableListOf<Client>()
    var mListTrade = mutableListOf<DataTradesOld>()
    val mListTradeOld = mutableListOf<DataTradesOld>()
    lateinit var mUsersSelectContractAdapter : UsersContractSelectedItemAdapter
    var selectedPosition: Int? = null
    var unSelectedPosition: Int? = null
    var selectType = ""
    var increment = 0
    var decrement = 0
    var selectionResult = 0
    var selectedIdArray = ArrayList<String>()
    var TAG = "UserContractSelectedItemActivity"
    lateinit var binding: ActivityUserContractSelectedItemBinding
    lateinit var viewModel: SalesPersonViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_user_contract_selected_item)
        viewModel= ViewModelProvider(this).get(SalesPersonViewModel::class.java)
        setData()
        initObserve()
    }

    fun initObserve() {
        viewModel.responsAllSalesPerson.observe(this, androidx.lifecycle.Observer
        { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mUsersSelectContractAdapter.notifyDataSetChanged()
                    mList.addAll(it.data!!.data.client)
                    Collections.reverse(mList)
//                    ListerReverser.reverse(mList as ArrayList<Client>?)
                    mUsersSelectContractAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading -> {
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responsDeleteSelectedSales.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted successfully")
                    mList.clear()
                    mUsersSelectContractAdapter.notifyDataSetChanged()
                    finish()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading -> {
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responsDeleteAllSales.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted successfully")
                    mList.clear()
                    mUsersSelectContractAdapter.notifyDataSetChanged()
                    finish()
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

    fun setData() {
        selectType = intent.getStringExtra("selectType").toString()
        mUsersSelectContractAdapter = UsersContractSelectedItemAdapter(this,selectType,
            mList,mList,this,this)

        binding.rvUsersSelectContract.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvUsersSelectContract.adapter = mUsersSelectContractAdapter

//        if (isInternetConnected(this)) {
//            presenter.getTradeDetails("sales", "1", "50")
//        }

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllSalesList("sales","1", " 20", "")
            }
        }
        clickEvents()
    }

    private fun clickEvents(){
        binding.mIvBack.setOnClickListener { finish() }
        binding.mIvRightMenuSelectContract.setOnClickListener {
            showRightMenu(it)
        }
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Edit")) {
            startActivity(
                Intent(this, AddSalesPersonActivity::class.java)
                    .putExtra("id", mList.get(position)._id)
                    .putExtra("type", mList.get(position).type)
            )
        }
        else  if (item.equals("main")) {
            startActivity(
                Intent(this, CustomerDetailActivity::class.java)
                    .putExtra("from","UserContrActivity")
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
//        else if (item.equals("Click")) {
//            if (isInternetConnected()) {
//                tab_click = mListTradeOld[position].name
//                if (mListTradeOld[position].name.equals("All")) {
//                    presenter.get_all_sales("1", " 20", "")
//                } else {
//                    presenter.get_all_sales(
//                        "1",
//                        " 20",
//                        mListTradeOld[position].name.toString().toLowerCase()
//                    )
//                }
//
//            }
//        }
    }

    private fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_download).isVisible = false

        selectionResult = increment - decrement

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
                                    viewModel.deleteSelectedSales(selectedIds)
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
                                viewModel.deleteAllSales("sales")
                            }
                        })
                }else{
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_edit) {
                if (selectedPosition!=null && selectionResult >= 1) {
                    startActivity(
                        Intent(this, AddSalesPersonActivity::class.java)
                            .putExtra("title", "Edit Users")
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

            var phone = Constants.insertString( mList[selectedPosition!!].phone_no, "", 0)
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

    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        unSelectedPosition = position
        selectedIdArray.removeAll(setOf(mList[position]._id))
        decrement = item as Int
    }
}