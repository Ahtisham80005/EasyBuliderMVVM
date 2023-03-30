package com.tradesk.activity.salesPerson

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.CheckModel
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
class UsersContrActivity : AppCompatActivity() , SingleListCLickListener, LongClickListener,
    CustomCheckBoxListener, UnselectCheckBoxListener {

    var checkBoxVisibility:Boolean=false
    var allCheckBoxSelect:Boolean=false
    var activeSlectMenu:Boolean=false

    var selectedPositionArray = ArrayList<Int>()

    var tab_click = "All"
    var mList = mutableListOf<Client>()
    var mListTrade = mutableListOf<DataTradesOld>()
    val mListTradeOld = mutableListOf<DataTradesOld>()
    var mcheckBoxModelList=mutableListOf<CheckModel>()
    val mUsersContractAdapter by lazy { UsersContractAdapter(this, mList, mList,this,this,this,this,checkBoxVisibility,allCheckBoxSelect,mcheckBoxModelList) }
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
            if(!activeSlectMenu)
            {
                if (mList.isNotEmpty()){
                    showRightMenu(it)
                }
            }
            else
            {
                showSecondRightMenu(it)
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
                    mList.addAll(it.data!!.data.client)
                    Collections.reverse(mList)
                    binding.rvUsersContract.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    activeSlectMenu=false
                    checkBoxVisibility=false
                    selectedIdArray.clear()
                    selectedPositionArray.clear()
                    mcheckBoxModelList.clear()
                    for(i in mList)
                    {
                        mcheckBoxModelList.add(CheckModel(false))
                    }
                    mUsersContractAdapter.checkboxVisibility=false
                    binding.rvUsersContract.adapter =mUsersContractAdapter
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
                    FirstTimeCallFunction()
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
                    FirstTimeCallFunction()
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
                mcheckBoxModelList.clear()
                for(i in mList)
                {
                    mcheckBoxModelList.add(CheckModel(false))
                }
                activeSlectMenu=true
                checkBoxVisibility=true
                selectedIdArray.clear()
                mUsersContractAdapter.checkboxVisibility=true
                mUsersContractAdapter.notifyDataSetChanged()

//                startActivity(
//                    Intent(this,UserContractSelectedItemActivity::class.java)
//                        .putExtra("selectType","single"))

            }else if (it.itemId == R.id.item_select_all){
//                startActivity(
//                    Intent(this,UserContractSelectedItemActivity::class.java)
//                        .putExtra("selectType","all"))

                mcheckBoxModelList.clear()
                for(i in mList)
                {
                    mcheckBoxModelList.add(CheckModel(true))
                }
                checkBoxVisibility=true
                activeSlectMenu=true
                selectedIdArray.clear()

                mUsersContractAdapter.checkboxVisibility=true
                mUsersContractAdapter.allCheckBoxSelect=true
                    //No Add List
                mUsersContractAdapter.notifyDataSetChanged()

//                Toast.makeText(this,mList.size.toString(),Toast.LENGTH_SHORT).show()
                if(mList.size<=1)
                {
                    selectedPositionArray.add(0)
                }

            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun  showSecondRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_download).isVisible = false

//        selectionResult = increment - decrement

        if (selectedIdArray.size > 1) {
            popup.menu.findItem(R.id.item_share).isVisible = false
            popup.menu.findItem(R.id.item_edit).isVisible = false
        }

//        if (selectType.equals("all")) {
//            popup.menu.findItem(R.id.item_share).isVisible = false
//        }

        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share) {
                if (selectedIdArray.size <= 1 && !selectedIdArray.isEmpty()) {
                    shareData()
                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_delete) {
                if (!selectedIdArray.isEmpty()) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this)) {
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedSales(selectedIds)
                                }
//                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                } else{
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_edit) {
                if (!selectedPositionArray.isEmpty() && selectedIdArray.size <= 1) {
                    startActivity(
                        Intent(this, AddSalesPersonActivity::class.java)
                            .putExtra("title", "Edit Users")
                            .putExtra("id", mList.get(selectedPositionArray.get(0))._id)
                            .putExtra("type", mList.get(selectedPositionArray.get(0)).type)
                    )
                }else{
                    toast("Select an item")
                }
            }
            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }
    override fun onCheckBoxClick(position: Int) {
//        Toast.makeText(this,position.toString(),Toast.LENGTH_SHORT).show()
        selectedPositionArray.add(position)
        selectedIdArray.add(mList[position]._id)
        Log.e("G_id",mList[position]._id) //63ecbd8f0303fe4963eee984
    }
    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
//        Toast.makeText(this,position.toString(),Toast.LENGTH_SHORT).show()
        selectedPositionArray.removeAll(setOf(position))
        selectedIdArray.removeAll(setOf(mList[position]._id))
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
    private fun showRightMenuOnLong(anchor: View,selectedPosition1:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_download).isVisible = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

        selectedPositionArray.clear()
        selectedIdArray.clear()
        selectedPositionArray.add(selectedPosition1)

//        popup.setOnDismissListener {
//            if (selectedPositionArray.removeAll(setOf(position))) {
//                selectedIdArray.removeAll(setOf(mList[selectedPosition!!]._id))
//            }
//        }
//        selectedPosition=selectedPosition1
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (!selectedPositionArray.isEmpty()) {
                    shareData()
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_delete) {
                if (!selectedPositionArray.isEmpty()) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this)) {
//                                itemPosition = selectedPosition
                                selectedIdArray.add(mList[selectedPositionArray.get(0)]._id)
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
                if (!selectedPositionArray.isEmpty()) {
                    startActivity(
                        Intent(this, AddSalesPersonActivity::class.java)
                            .putExtra("title", "Edit Users")
                            .putExtra("id", mList.get(selectedPositionArray.get(0))._id)
                            .putExtra("type", mList.get(selectedPositionArray.get(0)).type)
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

    private fun shareData(){
        if (!selectedPositionArray.isEmpty()) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")

            var phone = Constants.insertString(mList[selectedPositionArray.get(0)].phone_no, "", 0)
            phone = Constants.insertString(phone!!, ")", 2)
            phone = Constants.insertString(phone!!, " ", 3)
            phone = Constants.insertString(phone!!, "-", 7)
            phone = "+1(" + phone!!

            var shareMessage = """
                    ${
                "\n" + mList[selectedPositionArray.get(0)].name + "\n" +
                        mList[selectedPositionArray.get(0)].email + "\n" +
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
        FirstTimeCallFunction()
    }

    fun FirstTimeCallFunction()
    {
        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllSalesList("sales","1", " 100", "")
            }
        }
    }
}