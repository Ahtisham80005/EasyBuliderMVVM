package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.ClientSalesModelNew
import com.tradesk.Model.ClientsListModel
import com.tradesk.Model.SelectedIds
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesPersonViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responsAllSalesPerson: LiveData<NetworkResult<ClientsListModel>>
        get()=repository.responsAllSalesPerson

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel

    val responseUserDetail: LiveData<NetworkResult<ClientSalesModelNew>>
        get()=repository.responseUserDetail

    val responsAllSalesList: LiveData<NetworkResult<ClientsListModel>>
        get()=repository.responsAllSalesList

    val responsDeleteSelectedSales: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responsDeleteSelectedSales

    suspend fun getAllSalesPerson(type:String,page: String, limit: String) {
        repository.getAllSalesPerson(type,page,limit)
    }
    suspend fun getSalesPersonDetail(id: String, page: String, limit: String, type: String, status: String) {
        repository.getClientDetail(id, page, limit, type, status)
    }
    suspend fun addSalesPersonInJob(client: String, jobId: String) {
        repository.addSalesPersonInJob(client,jobId)
    }

    suspend fun deleteSelectedSales(selectedIds: SelectedIds) {
        repository.deleteSelectedSales(selectedIds)
    }

    suspend fun getAllSalesList(type:String,page: String, limit: String, trade: String) {
        repository.getAllSales(type, page, limit, trade)
    }
}