package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tradesk.Model.ClientSalesModelNew
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SalesUserViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseUserDetail: LiveData<NetworkResult<ClientSalesModelNew>>
    get()=repository.responseUserDetail

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
    get()=repository.responseSuccessModel

    val responseClientUpdateSuccessModel: LiveData<NetworkResult<SuccessModel>>
    get()=repository.responseClientUpdateSuccessModel


    suspend fun getUserDetails(id: String, page: String, limit: String, type: String, status: String) {
        repository.getUserDetails(id, page, limit, type, status)
    }

    suspend fun add_sales(map: HashMap<String, RequestBody>) {
        repository.addClient(map)
    }

    suspend fun updatesaleclient(map: HashMap<String, RequestBody>) {
        repository.updatesaleclient(map)
    }
}