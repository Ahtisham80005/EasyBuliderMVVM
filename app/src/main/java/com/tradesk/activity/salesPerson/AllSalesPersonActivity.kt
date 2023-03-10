package com.tradesk.activity.salesPerson

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.tradesk.Interface.SingleListCLickListener
import com.tradesk.Model.Client
import com.tradesk.R
import com.tradesk.adapter.SalesPersonsLeadsAdapter
import com.tradesk.databinding.ActivityAllSalesPersonBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.SalesPersonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllSalesPersonActivity : AppCompatActivity() , SingleListCLickListener {
    lateinit var binding:ActivityAllSalesPersonBinding
    lateinit var viewModel:SalesPersonViewModel
    val mList = mutableListOf<Client>()
    val mSalesPersonsLeadsAdapter by lazy { SalesPersonsLeadsAdapter(this, mList, this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_all_sales_person)
        viewModel= ViewModelProvider(this).get(SalesPersonViewModel::class.java)
        binding.rvSalesPersons.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvSalesPersons.adapter = mSalesPersonsLeadsAdapter
        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllSalesPerson("sales","1", "20")
            }
        }
        binding.mIvBack.setOnClickListener { finish() }
        binding.mBtnAddNew.setOnClickListener {
            val intent = Intent(this, AddSalesPersonActivity::class.java)
            intent.putExtra("title", "Add Sale person")
            intent.putExtra("screen_title", "Add User")
            startActivity(intent)
        }
        initObserve()
    }

    fun initObserve()
    {
        viewModel.responsAllSalesPerson.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    mList.clear()
                    mSalesPersonsLeadsAdapter.notifyDataSetChanged()
                    mList.addAll(it.data!!.data.client)
                    mSalesPersonsLeadsAdapter.notifyDataSetChanged()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this)
                }
            }
        })
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Users assigned for job successfully.")
                    val returnIntent = Intent()
                    setResult(RESULT_OK, returnIntent)
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

    override fun onSingleListClick(item: Any, position: Int) {
        if (intent.hasExtra("from"))
        {
            val builder = GsonBuilder()
            val gson = builder.create()
            val returnIntent = Intent()
            returnIntent.putExtra("result", mList[position]._id)
            returnIntent.putExtra("image", mList[position].image)
            returnIntent.putExtra("client", gson.toJson(mList[position]))
            setResult(RESULT_OK, returnIntent)
            finish()
        } else if (intent.hasExtra("fromjob"))
        {
            if (isInternetConnected(this)){
                Constants.showLoading(this)
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.addSalesPersonInJob(
                        mList[position]._id.toString(),
                        intent.getStringExtra("job_id").toString()
                    )
                }
           }
       }
    }

    override fun onResume() {
        super.onResume()
        if (isInternetConnected(this)) {
            Constants.showLoading(this)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getAllSalesPerson("sales","1", "150")
            }
        }
    }
}