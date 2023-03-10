package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.LeadDetailModel
import com.tradesk.Model.LeadsModel
import com.tradesk.Model.ProfileModel
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseProfileModel: LiveData<NetworkResult<ProfileModel>>
        get()=repository.responseProfileModel

    val responseAllLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responseAllLeadsModel

    val responsePendingLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responsePendingLeadsModel

    val responseFollowUPLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responseFollowUPLeadsModel

    val responseSaleLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responseSaleLeadsModel

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel

    val responseLeadDetailModel: LiveData<NetworkResult<LeadDetailModel>>
        get()=repository.responseLeadDetailModel


    suspend fun getAllLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getAllLead(type,page, limit, status)
    }
    suspend fun getPendingLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getPendingLead(type,page, limit, status)
    }
    suspend fun getFollowUPLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getFollowUPLead(type,page, limit, status)
    }
    suspend fun getSaleLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getSaleLead(type,page, limit, status)
    }
    suspend fun getProfile()
    {
        viewModelScope.launch {
            repository.getProfile()
        }
    }
    suspend fun getLeadDetail(id:String)
    {
        repository.getLeadDetail(id)
    }
    suspend fun LeadDelete(id:String)
    {
        repository.DeleteLead(id)
    }
}