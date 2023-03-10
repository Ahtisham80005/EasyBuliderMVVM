package com.tradesk.activity.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tradesk.R
import com.tradesk.databinding.ActivityForgotBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.ForgotViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotActivity : AppCompatActivity() {
    lateinit var binding:ActivityForgotBinding
    lateinit var viewModel: ForgotViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_forgot)
        viewModel=ViewModelProvider(this).get(ForgotViewModel::class.java)

        binding.imageView39.setOnClickListener { finish() }
        binding.imageView10.setOnClickListener {
            if (binding.etEmail.text.toString().trim().isEmpty()){
                toast("Enter email")
            }
            else{
                if (isInternetConnected(this)){
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.Forget(binding.etEmail.text.toString().trim())
                    }
                }
            }
        }
        initObserve()
    }

    fun initObserve()
    {
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast("Forget password mail sent successfully.")
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this@ForgotActivity)
                }
            }
        })
    }
}