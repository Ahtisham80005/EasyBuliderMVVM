package com.tradesk.activity.jobModule.Expense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradesk.Interface.LongClickListener
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Interface.UnselectCheckBoxListener
import com.tradesk.Model.Expenses
import com.tradesk.Model.SelectedIds
import com.tradesk.R
import com.tradesk.adapter.ExpensesSelectItemAdapter
import com.tradesk.databinding.FragmentExpenseMultiBinding
import com.tradesk.databinding.FragmentExpenseSingleBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.AllinOneDialog
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.ArrayList
import javax.inject.Inject


@AndroidEntryPoint
class ExpenseMultiFragment : Fragment(), SingleListCLickListener, UnselectCheckBoxListener,
    LongClickListener {

    val mList = mutableListOf<Expenses>()
    var mExpensesSelectAdapter: ExpensesSelectItemAdapter? = null
    var selectedPosition: Int? = null
    var unSelectedPosition: Int? = null
    var selectType :String? = null
    var increment = 0
    var decrement = 0
    var selectionResult = 0
    var selectedIdArray = ArrayList<String>()
    var TAG = "ExpenseMultiFragment"
    var bundle:Bundle? = null
    var jobIdBundle:String? = null

    lateinit var binding:FragmentExpenseMultiBinding
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
        binding= FragmentExpenseMultiBinding.inflate(inflater, container, false)
        setUp()
        initObserve()
        return binding.getRoot()
    }

    fun setUp() {
        bundle = arguments
        if (bundle != null){
            jobIdBundle = bundle!!.getString("job_id")
            selectType = bundle!!.getString("selectType")
            setData()
        }
    }

    fun initObserve() {
        viewModel.responseExpensesListModel.observe(requireActivity(), androidx.lifecycle.Observer { it ->
                Constants.hideLoading()
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data!!.data.expenses_list.isNotEmpty()) {
                            mList.clear()
                            mList.addAll(it.data!!.data.expenses_list)
                            mExpensesSelectAdapter?.notifyDataSetChanged()

                            var total = 0
                            it.data!!.data.expenses_list.forEach {
                                total += it.amount.toInt()
                            }
                            val formatter = DecimalFormat("#,###,###")
                            binding.tvTotalExpenseMulti.text = "$ "+formatter.format(total)
                        } else {
                            toast("No expense added yet.")
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

        viewModel.responseSuccessModel.observe(requireActivity(), androidx.lifecycle.Observer { it ->
                Constants.hideLoading()
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Deleted successfully")
                        mList.removeAt(selectedPosition!!)
                        mExpensesSelectAdapter?.notifyItemRemoved(selectedPosition!!)
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
                        mExpensesSelectAdapter?.notifyDataSetChanged()
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

    private fun setData() {
        mExpensesSelectAdapter = ExpensesSelectItemAdapter(requireContext(), selectType!!, mList, this, this,this)
        binding.rvExpensesSelected.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvExpensesSelected.adapter = mExpensesSelectAdapter

        if (isInternetConnected(requireActivity())) {
            Constants.showLoading(requireActivity())
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getExpenseslist("1", "20", jobIdBundle.toString())
            }

        }
        binding.mIvRightMenuExpenseSelect.setOnClickListener {
            showRightMenu(it)
        }
    }


    private fun showRightMenu(anchor: View): Boolean {
        val popup = PopupMenu(requireContext(), anchor)
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
            popup.menu.findItem(R.id.item_edit).isVisible = false
            popup.menu.findItem(R.id.item_delete).isVisible = true
        }

        popup.setOnMenuItemClickListener {

            if (it.itemId == R.id.item_share) {
                if (selectionResult >= 1) {
                    shareData()
                } else {
                    Constants.showToast(requireActivity(),"Select an item")
                }
            } else if (it.itemId == R.id.item_delete) {
                if (selectedPosition != null && selectionResult >= 1 && selectType.equals("single")) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete it ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            if (isInternetConnected(requireActivity()) && selectedPosition != null && selectionResult >= 1) {
                                val selectedIds = SelectedIds(selectedIdArray)
                                Constants.showLoading(requireActivity())
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.deleteSelectedExpense(selectedIds)
                                }
                                Log.d(TAG, "showRightMenu: "+selectedIdArray)
                            }
                        })
                } else if (isInternetConnected(requireActivity()) && selectType.equals("all")) {
                    AllinOneDialog(ttle = "Delete",
                        msg = "Are you sure you want to Delete all ?",
                        onLeftClick = {/*btn No click*/ },
                        onRightClick = {/*btn Yes click*/
                            Constants.showLoading(requireActivity())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteAllExpenseJob(jobIdBundle.toString())
                            }
                        })
                } else {
                    Constants.showToast(requireActivity(),"Select an item")
                }

            } else if (it.itemId == R.id.item_edit) {
                if (selectedPosition!=null && selectionResult >= 1) {
                    startActivity(
                        Intent(requireActivity(), AddExpenseActivity::class.java)
                            .putExtra("title", "Edit Expense")
                            .putExtra("expense_id", mList[selectedPosition!!]._id)
                            .putExtra("expense_title", mList[selectedPosition!!].title)
                            .putExtra("expense_amount", mList[selectedPosition!!].amount)
                            .putExtra("expense_image", mList[selectedPosition!!].image)
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

    private fun shareData() {
        if (selectedPosition != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tradesk\nLead Detail")
            var shareMessage = """
                    ${
                "\n" + mList[selectedPosition!!].title + "\n" +
                        mList[selectedPosition!!].amount + "\n" +
                        mList[selectedPosition!!].image + "\n"
                //        mList[selectedPosition!!].address + "\n" +
                //      mList[selectedPosition!!].trade + "\n"

            }        
               """.trimIndent()
            shareMessage = """
                    $shareMessage${"\nShared by - " + mPrefs.getKeyValue(PreferenceConstants.USER_NAME)}
                    """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        }
    }

    override fun onLongClickListener(item: Any, position: Int) {

    }

    override fun onSingleListClick(item: Any, position: Int) {
        selectedPosition = position
        selectedIdArray.add(mList[position]._id)
        increment = item as Int
        Log.d(TAG, "onSingleListClick: " + increment.toString())
    }

    override fun onCheckBoxUnCheckClick(item: Any, position: Int) {
        unSelectedPosition = position
        selectedIdArray.removeAll(setOf(mList[position]._id))
        decrement = item as Int
        Log.d(TAG, "onCheckBoxUnCheckClick: " + decrement.toString())
    }

    override fun onResume() {
        super.onResume()

    }

}