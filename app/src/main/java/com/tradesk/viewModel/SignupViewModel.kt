package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.LoginModel
import com.tradesk.Model.SignupModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseSignupModel: LiveData<NetworkResult<SignupModel>>
        get()=repository.responseSignupModel
    val responseLoginModel : LiveData<NetworkResult<LoginModel>>
        get()=repository.responseLoginModel

    suspend fun Signup(name:String,email:String,addres:String,password:String,userType:String,city:String,state:String,postal_code:String,latLong:String,trade:String)
    {
        viewModelScope.launch {
            repository.Signup(name,email,addres,password,userType,city,state,postal_code,trade,latLong)
        }
    }

    suspend fun SocialLogin(address: String, accessToken: String, device_token: String, device_type: String,
        post_code: String, city: String, state: String, latLong: String, loginType: String)
    {
        viewModelScope.launch {

        }
    }
}