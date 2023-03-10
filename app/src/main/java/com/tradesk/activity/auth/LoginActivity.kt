package com.tradesk.activity.auth

import android.content.Intent
import android.location.Address
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.messaging.FirebaseMessaging
import com.tradesk.MainActivity
import com.tradesk.R
import com.tradesk.databinding.ActivityLoginBinding
import com.tradesk.network.NetworkResult
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import com.tradesk.util.Constants
import com.tradesk.util.extension.toast
//import com.tradesk.util.extension.toast
import com.tradesk.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(){
    var token = ""
    var RC_SIGN_IN = 444
    lateinit var callbackManager: CallbackManager;
    private val EMAIL = "email"
    var mGoogleSignInClient: GoogleSignInClient? = null
    var addresses: List<Address>? = null
    var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    var postalCode = "unknown";
    var city = "unknown";
    var state = "unknown";
    var lat = ""
    var lng = ""
    var address = "unknown"
    var accessToken = "";
    var loginType=""
    var TAG = "LoginActivity"
    lateinit var binding:ActivityLoginBinding
    lateinit var viewModel:LoginViewModel
    @Inject
    lateinit var mPrefs: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)
        viewModel=ViewModelProvider(this).get(LoginViewModel::class.java)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if(result != null){
                token = result
                // DO your thing with your firebase token
            }
        }
        binding.mTvSignUp.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }
        binding.mTvForgot.setOnClickListener { startActivity(Intent(this, ForgotActivity::class.java)) }
        binding.mIvLogin.setOnClickListener {
            Constants.showLoading(this)
            if (token.isEmpty()) {
                FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                    if(result != null){
                        token = result
                        // DO your thing with your firebase token
                    }
                }
            }
            if (binding.etEmail.text.toString().trim().isEmpty()) {
                toast(getString(R.string.enter_email_address), false)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()) {
                toast(getString(R.string.enter_valid_address), false)
            } else if (binding.etPassword.visibility == View.VISIBLE && binding.etPassword.text.toString().trim()
                    .isEmpty()
            ) {
                toast(getString(R.string.enter_password), false)
            } /*else if (!PATTERN.matcher(etPassword.text.toString().trim()).matches()) {
                    toast(getString(R.string.password_length_message), false)
                } */ else {
                if (Constants.isInternetConnected(this))
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.Login(binding.etEmail.text.toString().trim(),
                            binding.etPassword.text.toString().trim(),
                            token, "android"
                        )
                    }
            }
        }
        viewModel.responseLoginModel.observe(this, androidx.lifecycle.Observer {it->
            Constants.hideLoading()
            when (it) {
                is NetworkResult.Success -> {
                    toast(it.data!!.message, true)
                    mPrefs.setUserLoggedIn(PreferenceConstants.USER_LOGGED_IN, true)
                    mPrefs.setKeyValue(PreferenceConstants.USER_TYPE, it.data.data.userType.toString())
                    mPrefs.setKeyValue(PreferenceConstants.USER_TOKEN, it.data.data.token ?: "")
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                is NetworkResult.Error -> {
                    toast(it.message)
                }
                is NetworkResult.Loading ->{
                    Constants.showLoading(this@LoginActivity)
                }
            }

//            if(data!=null && data is LoginModel)
//            {
//                toast(data.message, true)
//                mPrefs.setUserLoggedIn(PreferenceConstants.USER_LOGGED_IN, true)
//                mPrefs.setKeyValue(PreferenceConstants.USER_TYPE, data.data.userType.toString())
//                mPrefs.setKeyValue(PreferenceConstants.USER_TOKEN, data.data.token ?: "")
//                startActivity(Intent(this, MainActivity::class.java))
//                finishAffinity()
//            }
//            else{
//                toast(data.message)
//            }
        })
    }

//    override fun onSuccessful(objects: Objects) {
//
//    }
//    override fun onError(message: String) {
//
//    }
}