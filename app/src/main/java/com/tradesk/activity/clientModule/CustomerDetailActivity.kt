package com.tradesk.activity.clientModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.socialgalaxyApp.util.extension.loadWallImage
import com.tradesk.Model.ClientSalesModelNew
import com.tradesk.R
import com.tradesk.activity.salesPerson.AddSalesPersonActivity
import com.tradesk.databinding.ActivityCustomerDetailBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.insertString
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CustomerDetailActivity : AppCompatActivity() {
//    val mCustomerDocsAdapter by lazy { CustomerDocsAdapter(this, this) }
//    val mCustomerCompletedJobsAdapter by lazy { CustomerCompletedJobsAdapter(this, this) }
    lateinit var binding:ActivityCustomerDetailBinding
    lateinit var viewModel: CustomerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_customer_detail)
        viewModel=ViewModelProvider(this).get(CustomerViewModel::class.java)
        if (intent.getStringExtra("image").toString().isNotEmpty()) {
            binding.mTvImage.loadWallImage(intent.getStringExtra("image").toString())
        }

        if (intent.getStringExtra("phone").toString().isEmpty()) {
            binding.mTvPhone.text = "N/A"
        }
        else {

            binding.mTvPhone.text = insertString(intent.getStringExtra("phone").toString(), "", 0)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
            binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
            binding.mTvPhone.text = "+1 (" + binding.mTvPhone.text.toString()
        }

        if(intent.hasExtra("title"))
        {
            var title=intent.getStringExtra("title")
            if (intent.getStringExtra("from").equals("CustomerActivity")) {
                binding.textView6.text="Client Profile"
            }
        }
        if(intent.hasExtra("from")) {
            if (intent.getStringExtra("from").equals("CustomerActivity")) {
                binding.mTvTrade.visibility= View.GONE
                binding.mTvCompany.visibility= View.GONE
                binding.mTvAddress.visibility= View.VISIBLE
                binding.textView104.visibility= View.VISIBLE
                binding.divider21.visibility= View.VISIBLE
                binding.textViewTrade.text="Notes"
            }
        }

        binding.mEditUser.setOnClickListener{
            if(intent.hasExtra("from"))
            {
                if(intent.getStringExtra("from").equals("CustomerActivity"))
                {
                    startActivity(
                        Intent(this, AddClientActivity::class.java)
                            .putExtra("title", "Edit Client")
                            .putExtra("id", intent.getStringExtra("id"))
                            .putExtra("type",intent.getStringExtra("type")))
                }
            }
        }
        if (intent.hasExtra("id")) {
            Constants.showLoading(this@CustomerDetailActivity)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getClientDetail(intent.getStringExtra("id").toString(), "1", "10", intent.getStringExtra("type").toString(), "")
            }
        }
        binding.mIvBack.setOnClickListener { finish() }
        initObserver()
    }

    fun initObserver() {
        viewModel.responseUserDetail.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    setUserDetails(it.data!!)
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

    private fun setUserDetails(it: ClientSalesModelNew) {
        if (it.data.client.image.isNotEmpty()) {
            binding.mTvImage.loadWallImage(it.data.client.image)
        }
        binding.mTvName.text = it.data.client.name
        binding.mNameTv.text =it.data.client.name
        binding.mTvTrade.text = it.data.client.trade
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.textTrade.text =it.data.client.trade
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.mTvCompany.text = it.data.client.name
        binding.mTvEmail.text = it.data.client.email
        binding.mTvAddress.text = it.data.client.address.street

        binding.mTvPhone.text = insertString(it.data.client.phone_no, "", 0)
        binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), ")", 2)
        binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), " ", 3)
        binding.mTvPhone.text = insertString(binding.mTvPhone.text.toString(), "-", 7)
        binding.mTvPhone.text = "+1 (" + binding.mTvPhone.text.toString()


        if(intent.hasExtra("from")) {
            if (intent.getStringExtra("from").equals("CustomerActivity"))
            {
                binding.textTrade.text =it.data.client.privatenotes
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("id")) {
            Constants.showLoading(this@CustomerDetailActivity)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getClientDetail(intent.getStringExtra("id").toString(), "1", "10", intent.getStringExtra("type").toString(), "")
            }
        }
    }

}