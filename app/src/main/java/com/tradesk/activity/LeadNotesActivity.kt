package com.tradesk.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tradesk.R
import com.tradesk.databinding.ActivityLeadNotesBinding
import com.tradesk.viewModel.AddLeadViewModel
import com.tradesk.viewModel.LeadNotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeadNotesActivity : AppCompatActivity() {
    lateinit var binding:ActivityLeadNotesBinding
    lateinit var viewModel:LeadNotesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_lead_notes)
        viewModel= ViewModelProvider(this).get(LeadNotesViewModel::class.java)
    }
}