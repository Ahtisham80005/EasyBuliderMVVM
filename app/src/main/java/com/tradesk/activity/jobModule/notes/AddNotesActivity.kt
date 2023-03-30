package com.tradesk.activity.jobModule.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tradesk.R
import com.tradesk.databinding.ActivityAddNotesBinding
import com.tradesk.network.NetworkResult
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.JobsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddNotesActivity : AppCompatActivity() {
    lateinit var binding:ActivityAddNotesBinding
    lateinit var viewModel:JobsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_notes)
        viewModel= ViewModelProvider(this).get(JobsViewModel::class.java)
        binding.mIvBack.setOnClickListener { finish() }
        binding.mBtnSubmit.setOnClickListener {

            if (binding.mEtTitle.text.toString().trim().isEmpty()) {
                toast("Enter note title", false)
            } else if (binding.mEtDescription.text.toString().trim().isEmpty()) {
                toast("Enter note description", false)
            } else {
                if (isInternetConnected(this)) {
                    Constants.showLoading(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.addleadsnotes(
                            intent.getStringExtra("id")!!,
                            binding.mEtTitle.text.toString().trim(),
                            binding.mEtDescription.text.toString().trim()
                        )
                    }
                }
            }
        }
        initObserve()
    }

    fun initObserve() {
        viewModel.responseSuccessModel.observe(this, androidx.lifecycle.Observer { it ->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message)
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
}