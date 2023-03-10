package com.tradesk.activity.proposalModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.tradesk.R
import com.tradesk.databinding.ActivityPaymentSchduleBinding
import com.tradesk.util.extension.toast

class PaymentSchduleActivity : AppCompatActivity() {
    lateinit var binding:ActivityPaymentSchduleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_payment_schdule)
        binding.addPayment.setOnClickListener { toast("Coming soon") }
    }
}