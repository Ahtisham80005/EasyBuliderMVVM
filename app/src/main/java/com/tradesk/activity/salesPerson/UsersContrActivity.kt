package com.tradesk.activity.salesPerson

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
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.Client
import com.tradesk.Model.DataTradesOld
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.activity.clientModule.CustomerDetailActivity
import com.tradesk.adapter.UsersContractAdapter
import com.tradesk.databinding.ActivityUsersContrBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.addWatcher
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.SalesPersonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UsersContrActivity : AppCompatActivity() , SingleListCLickListener, LongClickListener {
    var tab_click = "All"
    var mList = mutableListOf<Client>()
    var mListTrade = mutableListOf<DataTradesOld>()
    val mListTradeOld = mutableListOf<DataTradesOld>()
    val mUsersContractAdapter by lazy { UsersContractAdapter(this, mList, mList,this,this) }
    var selectedIdArray = ArrayList<String>()
    var itemPosition: Int? = null
    lateinit var binding:ActivityUsersContrBinding
    lateinit var viewModel:SalesPersonViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_users_contr)
        viewModel=ViewModelProvider(this).get(SalesPersonViewModel::class.java)
        binding.mIvBack.setOnClickListener { finish() }
        binding.rvUsersContract.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvUsersContract.adapter = mUsersContractAdapter
        binding.mIvRightMenuContract.setOnClickListener {
            if (mList.isNotEmpty()){
                showRightMenu(it)
            }
        }
        binding.mIvAdd.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddSalesPersonActivity::class.java)
                    .putExtra("title","Add Sale person")
                    .putExtra("addUser",true)
            )
        }
//        if (isInternetConnected(this)) {
//            presenter.getTradeDetails("sales", "1", "100")
//        }

        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllSalesList("sales","1", " 100", "")
            }
        }
        binding.mEtSearchName.addWatcher {
            mUsersContractAdapter.filter.filter(it)
        }
        initObserve()
    }

    fun initObserve() {
        viewModel.responsAllSalesPerson.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mUsersContractAdapter.notifyDataSetChanged()
                    mList.addAll(it.data!!.data.client)
                    Collections.reverse(mList)
                    mUsersContractAdapter.notifyDataSetChanged()
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
                    mList.removeAt(itemPosition!!)
                    mUsersContractAdapter.notifyItemRemoved(itemPosition!!)
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
    fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.sub_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{
            if (it.itemId == R.id.item_select_items){
                startActivity(
                    Intent(this,UserContractSelectedItemActivity::class.java)
                        .putExtra("selectType","single"))
            }else if (it.itemId == R.id.item_select_all){
                startActivity(
                    Intent(this,UserContractSelectedItemActivity::class.java)
                        .putExtra("selectType","all"))
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
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
                Intent(this, SalesPersonDetailActivity::class.java)
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

    override fun onLongClickListener(item: Any, position: Int) {
        showRightMenuOnLong(item as View,position)
    }
    private fun showRightMenuOnLong(anchor: View,selectedPosition:Int): Boolean {
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
                if (selectedPosition != null ) {
                    shareData(selectedPosition)
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
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedSales(selectedIds)
                                }
                            }
                        })
                }else{
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_edit) {
                if (selectedPosition!=null) {
                    startActivity(
                        Intent(this, AddSalesPersonActivity::class.java)
                            .putExtra("title", "Edit Users")
                            .putExtra("id", mList.get(selectedPosition)._id)
                            .putExtra("type", mList.get(selectedPosition).type)
                    )
                    //   finish()
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
                viewModel.getAllSalesList("sales","1", " 100", "")
            }
        }
    }
}