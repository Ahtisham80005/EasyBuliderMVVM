package com.tradesk.activity.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tradesk.R
import com.tradesk.databinding.ActivityChangePasswordBinding
import com.tradesk.util.Constants
import com.tradesk.util.Constants.isInternetConnected
import com.tradesk.util.extension.toast
import com.tradesk.viewModel.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    lateinit var binding:ActivityChangePasswordBinding
    lateinit var viewModel: SignupViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_change_password)
        viewModel= ViewModelProvider(this).get(SignupViewModel::class.java)
        binding.mIvBack.setOnClickListener { finish() }
        binding.textView6.setText(intent.getStringExtra("type").toString())
        if (intent.getStringExtra("type").equals("Change Password")){
            binding.constraintLayout17.visibility= View.VISIBLE
            binding.terms.visibility= View.GONE
        }else if(intent.getStringExtra("type").equals("Terms $ Conditions")){
            binding.constraintLayout17.visibility= View.GONE
            binding.terms.visibility= View.VISIBLE
            if (isInternetConnected(this)){
                CoroutineScope(Dispatchers.IO).launch {
//                    viewModel.terms()
                }
            }
        }else if (intent.getStringExtra("type").equals("Privacy Policy")){
            binding.constraintLayout17.visibility= View.GONE
            binding.terms.visibility= View.VISIBLE
            if (isInternetConnected(this)){
                CoroutineScope(Dispatchers.IO).launch {
//                    viewModel.terms()
                }
            }
        }
        binding.ivSubmit.setOnClickListener {
            if (binding.etOldPassword.text.toString().trim().isEmpty()) {
                toast("Enter old password", false)
            }  else if (!Constants.PATTERN.matcher(binding.etOldPassword.text.toString().trim()).matches()) {
                toast("Password must contain upper and lower letters, numerics, and a special character with a min of 8 characters", false)
            }  else if (binding.etCPassword.text.toString().trim().isEmpty()) {
                toast("Enter new password", false)
            }  else if (!Constants.PATTERN.matcher(binding.etCPassword.text.toString().trim()).matches()) {
                toast("Password must contain upper and lower letters, numerics, and a special character with a min of 8 characters", false)
            }  else if (binding.etConfirmPassword.text.toString().trim().isEmpty()) {
                toast("Enter confirm password", false)
            }  else if (binding.etConfirmPassword.text.toString().trim().equals(binding.etCPassword.text.toString().trim()).not()) {
                toast("Password does not match", false)
            }else{
                if (isInternetConnected(this))
                    CoroutineScope(Dispatchers.IO).launch {
//                        viewModel.changepassword(binding.etOldPassword.text.toString().trim(),binding.etCPassword.text.toString().trim())
                    }
            }

        }
    }
}