package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tradesk.Model.ClientSalesModelNew
import com.tradesk.Model.ClientsListModel
import com.tradesk.Model.SelectedIds
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responsAllClient: LiveData<NetworkResult<ClientsListModel>>
        get()=repository.responsAllSalesPerson

    val responseUserDetail: LiveData<NetworkResult<ClientSalesModelNew>>
        get()=repository.responseUserDetail

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel

    val responsDeleteSelectedClient: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responsDeleteSelectedSales

    val responseClientUpdateSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseClientUpdateSuccessModel

    suspend fun getAllClient(type:String,page: String, limit: String) {
        repository.getAllClients(type,page,limit)
    }
    suspend fun getClientDetail(id: String, page: String, limit: String, type: String, status: String) {
        repository.getClientDetail(id, page, limit, type, status)
    }
    suspend fun addClient(map:HashMap<String, RequestBody>) {
        repository.addClient(map)
    }

    suspend fun updateClient(map:HashMap<String, RequestBody>)
    {
        repository.updatesaleclient(map)
    }

    suspend fun deleteSelectedClient(selectedIds: SelectedIds) {
        repository.deleteSelectedSales(selectedIds)
    }
}