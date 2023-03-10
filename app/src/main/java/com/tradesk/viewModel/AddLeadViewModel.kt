package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AddLeadViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel

    suspend fun AddLeads(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody?>?)
    {
        viewModelScope.launch {
            repository.AddLeads(map, list)
        }
    }
    suspend fun updateLeads(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody?>?) {
        viewModelScope.launch {
            repository.updateLeads(map, list)
        }
    }
}