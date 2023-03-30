package com.tradesk.activity.jobModule.Expense

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.tradesk.Interface.CustomCheckBoxListener
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.CheckModel
import com.tradesk.Model.Expenses
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.activity.ImageActivity
import com.tradesk.adapter.ExpensesAdapter
import com.tradesk.databinding.FragmentExpenseSingleBinding
import com.tradesk.databinding.FragmentHomeBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import com.tradesk.viewModel.LeadViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseSingleFragment : Fragment(), SingleListCLickListener, LongClickListener ,
    CustomCheckBoxListener, UnselectCheckBoxListener {


    var checkBoxVisibility:Boolean=false
    var allCheckBoxSelect:Boolean=false
    var activeSlectMenu:Boolean=false


    val mList = mutableListOf<Expenses>()
    var mcheckBoxModelList=mutableListOf<CheckModel>()
    val mProposalsAdapter by lazy { ExpensesAdapter(requireContext(),mList,this,this,this,this,checkBoxVisibility,allCheckBoxSelect,mcheckBoxModelList) }
    var itemPosition: Int? = null
    var selectedIdArray = ArrayList<String>()
    var selectedPositionArray = ArrayList<Int>()
    var TAG = "ExpenseSingleFragment"
    var bundle:Bundle? = null
    var jobIdBundle:String? = null

    lateinit var binding:FragmentExpenseSingleBinding
    lateinit var viewModel: JobsViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentExpenseSingleBinding.inflate(inflater, container, false)
        setUp()
        initObserve()
        return binding.getRoot()
    }

    fun initObserve() {
        viewModel.responseExpensesListModel.observe(requireActivity(), androidx.lifecycle.Observer { it ->
                Constants.hideLoading()
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data!!.data.expenses_list.isNotEmpty()){
                            mList.clear()
                            mList.addAll(it.data!!.data.expenses_list)
                            binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            activeSlectMenu=false
                            checkBoxVisibility=false
                            selectedIdArray.clear()
                            selectedPositionArray.clear()
                            mcheckBoxModelList.clear()
                            for(i in mList)
                            {
                                mcheckBoxModelList.add(CheckModel(false))
                            }
                            mProposalsAdapter.checkboxVisibility=false
                            binding.rvExpenses.adapter = mProposalsAdapter
                            mProposalsAdapter.notifyDataSetChanged()
                            setTotalExpense()
                        }else{
                            Toast.makeText(requireContext(),"No expense added yet.",Toast.LENGTH_SHORT).show()
                            mList.clear()
                            mProposalsAdapter.notifyDataSetChanged()
                            setTotalExpense()
                        }
                    }
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loading -> {
                        Constants.showLoading(requireActivity())
                    }
                }
            })
        viewModel.responseSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted successfully")
                    if (isInternetConnected(requireActivity())){
                        Constants.showLoading(requireActivity())
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.getExpenseslist("1","20",jobIdBundle.toString())
                        }
                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading -> {
                    Constants.showLoading(requireActivity())
                }
            }
        })
        viewModel.responsedeleteAllExpenseJobSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Deleted successfully")
                    mList.clear()
                    mProposalsAdapter?.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading -> {
                    Constants.showLoading(requireActivity())
                }
            }
        })
    }

    fun setUp() {
        bundle = arguments
        jobIdBundle = bundle!!.getString("job_id")

        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvExpenses.adapter = mProposalsAdapter

        // mIvBack.setOnClickListener { finish() }

        binding.mIvAddExpense.setOnClickListener {

            requireActivity().startActivity(
            Intent(requireActivity(),AddExpenseActivity::class.java)
                //   .putExtra("job_id",intent.getStringExtra("job_id")))
                .putExtra("job_id",jobIdBundle))
        }

        binding.mIvRightMenuExpense.setOnClickListener {
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
    }

    fun showRightMenu(anchor: View): Boolean {

        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.sub_gallery_menu, popup.getMenu())
        popup.setOnMenuItemClickListener{

            if (it.itemId == R.id.item_select_items){
//                val mFragment = ExpenseMultiFragment()
//                val mBundle = Bundle()
//                mBundle.putString("job_id",jobIdBundle)
//                mBundle.putString("selectType","single")
//                mFragment.arguments = mBundle
//
//                val mFragmentManager =requireActivity().supportFragmentManager
//                val mFragmentTransaction = mFragmentManager.beginTransaction()
//                mFragmentTransaction.replace(R.id.mainContainer,mFragment)
//                mFragmentTransaction.addToBackStack(null)
//                mFragmentTransaction.commit()

                mcheckBoxModelList.clear()
                for(i in mList)
                {
                    mcheckBoxModelList.add(CheckModel(false))
                }
                selectedIdArray.clear()
                activeSlectMenu=true
                checkBoxVisibility=true
                mProposalsAdapter.checkboxVisibility=true
                mProposalsAdapter.notifyDataSetChanged()
            }else if (it.itemId == R.id.item_select_all){

//                val mFragment = ExpenseMultiFragment()
//                val mBundle = Bundle()
//                mBundle.putString("job_id",jobIdBundle)
//                mBundle.putString("selectType","all")
//                mFragment.arguments = mBundle
//
//                val mFragmentManager = requireActivity().supportFragmentManager
//                val mFragmentTransaction = mFragmentManager.beginTransaction()
//                mFragmentTransaction.replace(R.id.mainContainer,mFragment)
//                mFragmentTransaction.addToBackStack(null)
//                mFragmentTransaction.commit()

                mcheckBoxModelList.clear()
                for(i in mList)
                {
                    mcheckBoxModelList.add(CheckModel(true))
                }
                checkBoxVisibility=true
                activeSlectMenu=true
                selectedIdArray.clear()
                    //From Job
                mProposalsAdapter.checkboxVisibility=true
                mProposalsAdapter.allCheckBoxSelect=true
                    //No Add List
                mProposalsAdapter.notifyDataSetChanged()

            }else if(it.itemId == R.id.item_delete_all){
                if (isInternetConnected(requireActivity())) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete all ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            //  presenter.deleteAllExpenseJob(intent.getStringExtra("job_id").toString())\
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllExpenseJob(jobIdBundle.toString())
                            }
                        })
                }
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun showSecondRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())

        popup.menu.findItem(R.id.item_download).isVisible = false

//        selectionResult = increment - decrement
//
//        Log.d(TAG, "showRightMenu: " + selectionResult.toString())
//        Log.d(TAG, "selectedposition: " + selectedPosition.toString())
//        Log.d(TAG, "unselectedposition: " + unSelectedPosition.toString())


        if (selectedIdArray.size>1) {
            popup.menu.findItem(R.id.item_share).isVisible = false
            popup.menu.findItem(R.id.item_edit).isVisible = false
            popup.menu.findItem(R.id.item_delete).isVisible = true
        }

        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share) {
                if (selectedIdArray.size <= 1 && !selectedIdArray.isEmpty()) {
                    shareData()
                } else {
                    Constants.showToast(requireActivity(),"Select an item")
                }
            }
            else if (it.itemId == R.id.item_delete) {

                if (!selectedPositionArray.isEmpty() && selectedIdArray.size >= 1)
                {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(requireActivity()) ) {
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(requireActivity())
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedExpense(selectedIds)
                                }
                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                }
//                else if (isInternetConnected(requireActivity()) && selectType.equals("all")) {
//                    AllinOneDialog(ttle = "Delete",
//                        msg = "Are you sure you want to Delete all ?",
//                        onLeftClick = {/*btn No click*/ },
//                        onRightClick = {/*btn Yes click*/
//                            Constants.showLoading(requireActivity())
//                            CoroutineScope(Dispatchers.IO).launch {
//                                viewModel.deleteAllExpenseJob(jobIdBundle.toString())
//                            }
//                        })
//                }
                else {
                    Constants.showToast(requireActivity(),"Select an item")
                }

            } else if (it.itemId == R.id.item_edit) {
                if (!selectedPositionArray.isEmpty() && selectedIdArray.size >= 1) {
                    startActivity(
                        Intent(requireActivity(), AddExpenseActivity::class.java)
                            .putExtra("title", "Edit Expense")
                            .putExtra("expense_id", mList[selectedPositionArray.get(0)]._id)
                            .putExtra("expense_title", mList[selectedPositionArray.get(0)].title)
                            .putExtra("expense_amount", mList[selectedPositionArray.get(0)].amount)
                            .putExtra("expense_image", mList[selectedPositionArray.get(0)].image)
                    )

                    //  finish()
                }else{
                    Constants.showToast(requireActivity(),"Select an item")
                }
            }

            return@setOnMenuItemClickListener true
        }
        popup.show()
        return true
    }

    private fun setTotalExpense(){
        var total = 0
        mList.forEach {
            total += it.amount.toInt()
        }
        val formatter = DecimalFormat("#,###,###")
        binding.tvTotalExpense.text = "$ "+formatter.format(total)
    }

    override fun onCheckBoxClick(position: Int) {
//        selectedPosition=position
        selectedPositionArray.add(position)
        selectedIdArray.add(mList[position]._id)
    }
    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        selectedIdArray.removeAll(setOf(mList[position]._id))
        selectedPositionArray.removeAll(setOf(position))
    }

    override fun onLongClickListener(item: Any, position: Int) {
        showRightMenuLongClick(item as View,position)
    }

    override fun onSingleListClick(item: Any, position: Int) {
        if (item.equals("Image")){
            startActivity(Intent(requireActivity(), ImageActivity::class.java)
                .putExtra("expense","Expenses")
                .putExtra("image",mList[position].image)
            )
        } else {
            var expenses:Expenses= item as Expenses;
            val builder = GsonBuilder()
            val gson = builder.create()
            var string=gson.toJson(expenses);
            startActivity(Intent(requireActivity(),AddExpenseActivity::class.java)
                //  .putExtra("job_id",intent.getStringExtra("job_id"))
                .putExtra("job_id",jobIdBundle)
                .putExtra("expenses",string))

        }
    }

    private fun showRightMenuLongClick(anchor: View,selectedPosition1:Int): Boolean {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.select_item_proposal_menu, popup.getMenu())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.END
        }
        popup.menu.findItem(R.id.item_download).isVisible = false


        selectedPositionArray.clear()
        selectedIdArray.clear()

        selectedPositionArray.add(selectedPosition1)

//        popup.setOnDismissListener {
//            if (selectedPositionArray.isEmpty() != null) {
//                selectedIdArray.removeAll(setOf(mList[selectedPosition!!]._id))
//            }
//        }

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
                            if (isInternetConnected(requireActivity())) {
//                                itemPosition = selectedPosition
                                selectedIdArray.add(mList[selectedPositionArray.get(0)]._id)
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(requireActivity())
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedExpense(selectedIds)
                                }
                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                } else {
                    toast("Select an item")
                }

            } else if (it.itemId == R.id.item_edit) {
                if (!selectedPositionArray.isEmpty()) {
                    startActivity(
                        Intent(requireActivity(), AddExpenseActivity::class.java)
                            .putExtra("title", "Edit Expense")
                            .putExtra("expense_id", mList[selectedPositionArray.get(0)]._id)
                            .putExtra("expense_title", mList[selectedPositionArray.get(0)].title)
                            .putExtra("expense_amount", mList[selectedPositionArray.get(0)].amount)
                            .putExtra("expense_image", mList[selectedPositionArray.get(0)].image)
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

    private fun shareData() {
        if (!selectedPositionArray.isEmpty()) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
            var shareMessage = """
                    ${
                "\n" + mList[selectedPositionArray.get(0)].title + "\n" +
                        mList[selectedPositionArray.get(0)].amount + "\n" +
                        mList[selectedPositionArray.get(0)].image + "\n"
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
        if (isInternetConnected(requireActivity())){
            Constants.showLoading(requireActivity())
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getExpenseslist("1","20",jobIdBundle.toString())
            }
        }
    }

}