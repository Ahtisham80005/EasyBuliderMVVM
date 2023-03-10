package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tradesk.Model.LoginModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseLoginModel : LiveData<NetworkResult<LoginModel>>
        get()=repository.responseLoginModel

    suspend fun Login(email: String, password: String, device_token: String, device_type: String)
    {
        repository.Login(email, password, device_token, device_type)
    }

}