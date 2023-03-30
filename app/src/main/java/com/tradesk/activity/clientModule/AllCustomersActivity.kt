package com.tradesk.activity.clientModule

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
import com.google.gson.GsonBuilder
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.CheckModel
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
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AllCustomersActivity : AppCompatActivity(), SingleListCLickListener, LongClickListener,
    CustomCheckBoxListener, UnselectCheckBoxListener {

    var checkBoxVisibility:Boolean=false
    var allCheckBoxSelect:Boolean=false
    var activeSlectMenu:Boolean=false

    var selectedPositionArray = ArrayList<Int>()
    companion object{
        lateinit var context: AllCustomersActivity
    }
    lateinit var binding:ActivityAllCustomersBinding
    lateinit var viewModel:CustomerViewModel
    val mList = mutableListOf<Client>()
    var mcheckBoxModelList=mutableListOf<CheckModel>()
    val mCustomersAdapter by lazy { CustomersAdapter(this, mList, mList, this,this,this,this,checkBoxVisibility,allCheckBoxSelect,mcheckBoxModelList) }
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
                    Collections.reverse(mList)
                    mcheckBoxModelList.clear()
                    for(i in mList)
                    {
                        mcheckBoxModelList.add(CheckModel(false))
                    }
                    binding.rvCustomers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    activeSlectMenu=false
                    checkBoxVisibility=false
                    selectedIdArray.clear()
                    selectedPositionArray.clear()
                    mCustomersAdapter.checkboxVisibility=false
                    binding.rvCustomers.adapter =mCustomersAdapter
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
                    FirstTimeRunCode()
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

    fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.sub_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{

            if (it.itemId == R.id.item_select_items){

//                startActivity(
//                    Intent(this, SelectedItemCustomerActivity::class.java)
//                        .putExtra("selectType","single"))

                mcheckBoxModelList.clear()
                for(i in mList)
                {
                    mcheckBoxModelList.add(CheckModel(false))
                }
                activeSlectMenu=true
                checkBoxVisibility=true
                selectedIdArray.clear()
                mCustomersAdapter.checkboxVisibility=true
                mCustomersAdapter.notifyDataSetChanged()


            }else if (it.itemId == R.id.item_select_all){

//                startActivity(
//                    Intent(this, SelectedItemCustomerActivity::class.java)
//                        .putExtra("selectType","all"))

                mcheckBoxModelList.clear()
                for(i in mList)
                {
                    mcheckBoxModelList.add(CheckModel(true))
                }
                checkBoxVisibility=true
                activeSlectMenu=true
                selectedIdArray.clear()
                mCustomersAdapter.checkboxVisibility=true
                mCustomersAdapter.allCheckBoxSelect=true
                //No Add List
                mCustomersAdapter.notifyDataSetChanged()

                //For One Item
                if(mList.size<=1)
                {
                    selectedPositionArray.add(0)
                }


            }
//            else if(it.itemId == R.id.item_delete_all){
//                if (isInternetConnected(this)) {
//                    AllinOneDialog(ttle = "Delete",
//                        msg = "Are you sure you want to Delete all ?",
//                        onLeftClick = {/*btn No click*/ },
//                        onRightClick = {/*btn Yes click*/
//                            presenter.deleteAllClient("client")
//                        })
//                }
//            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun showSecondRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())

        popup.menu.findItem(R.id.item_download).isVisible = false

//        selectionResult = increment - decrement
//
//        Log.d(TAG, "showRightMenu: " + selectionResult.toString())
//        Log.d(TAG, "selectedposition: " + selectedPosition.toString())
//        Log.d(TAG, "unselectedposition: " + unSelectedPosition.toString())

        if (selectedIdArray.size> 1) {
            popup.menu.findItem(R.id.item_share).isVisible = false
            popup.menu.findItem(R.id.item_edit).isVisible = false
        }

//        if (selectType.equals("all")) {
//            popup.menu.findItem(R.id.item_share).isVisible = false
//
//        }

        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share) {
                if (selectedIdArray.size <= 1 && !selectedIdArray.isEmpty()) {
                    shareData()
                } else {
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_delete) {
                if (!selectedIdArray.isEmpty()) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(this)) {
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedClient(selectedIds)
                                }
//                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                }
//                else if (isInternetConnected(this) && selectType.equals("all")) {
//                    AllinOneDialog(ttle = "Delete",
//                        msg = "Are you sure you want to Delete all ?",
//                        onLeftClick = {/*btn No click*/ },
//                        onRightClick = {/*btn Yes click*/
//                            Constants.showLoading(this)
//                            CoroutineScope(Dispatchers.IO).launch {
//                                viewModel.deleteAllClient("client")
//                            }
//                        })
//                }
                else{
                    toast("Select an item")
                }
            } else if (it.itemId == R.id.item_edit) {
                if (!selectedIdArray.isEmpty()) {
                    startActivity(
                        Intent(this, AddClientActivity::class.java)
                            .putExtra("title", "Edit Client")
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
        selectedPositionArray.add(position)
        selectedIdArray.add(mList[position]._id)
        Log.e("G_id",mList[position]._id) //63ecbd8f0303fe4963eee984
    }
    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        selectedPositionArray.removeAll(setOf(position))
        selectedIdArray.removeAll(setOf(mList[position]._id))
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

    private fun showRightMenuLongClick(anchor: View,selectedPosition1:Int): Boolean {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        popup.menu.findItem(R.id.item_download).isVisible = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }

//        popup.setOnDismissListener {
//            if (selectedPosition != null) {
//                selectedIdArray.removeAll(setOf(mList[selectedPosition]._id))
//            }
//        }

        selectedPositionArray.clear()
        selectedIdArray.clear()
        selectedPositionArray.add(selectedPosition1)

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_share) {
                if (!selectedPositionArray.isEmpty()) {
                    shareData()
                } else {
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_delete) {
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
                                    viewModel.deleteSelectedClient(selectedIds)
                                }
                            }
                        })
                }else{
                    toast("Select an item")
                }
            }
            else if (it.itemId == R.id.item_edit) {
                if (!selectedPositionArray.isEmpty()) {
                    startActivity(
                        Intent(this, AddClientActivity::class.java)
                            .putExtra("title", "Edit Client")
                            .putExtra("id", mList.get(selectedPositionArray.get(0))._id)
                            .putExtra("type", mList.get(selectedPositionArray.get(0)).type)
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
                        mList[selectedPositionArray.get(0)].address.street + "\n" +
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
        FirstTimeRunCode()
    }

    fun FirstTimeRunCode()
    {
        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllClient("client","1", "50")
            }
        }
    }


}