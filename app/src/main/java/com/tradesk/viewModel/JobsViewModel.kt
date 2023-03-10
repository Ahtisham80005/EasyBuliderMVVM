package com.tradesk.viewModel

import androidx.lifecycle.LiveData
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
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseProfileModel: LiveData<NetworkResult<ProfileModel>>
        get()=repository.responseProfileModel

    val responseAllLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responseAllLeadsModel

    val responsePendingLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responsePendingLeadsModel

    val responseOngoingLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responseFollowUPLeadsModel

    val responseCompletedLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=repository.responseSaleLeadsModel

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel

    val responseLeadDetailModel: LiveData<NetworkResult<LeadDetailModel>>
        get()=repository.responseLeadDetailModel

    val responseJobConvertSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseLeadConvertSuccessModel

    suspend fun getAllLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getAllLead(type,page, limit, status)
    }
    suspend fun getPendingLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getPendingLead(type,page, limit, status)
    }
    suspend fun getOngoingLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getFollowUPLead(type,page, limit, status)
    }
    suspend fun getCompletedLeads(type:String,page: String, limit: String, status: String)
    {
        repository.getSaleLead(type,page, limit, status)
    }
    suspend fun getProfile()
    {
        viewModelScope.launch {
            repository.getProfile()
        }
    }

    suspend fun convertJobs(id: String, type: String, status: String, converted_to_job: String)
    {
        if (status.equals("complete", true)||status.equals("completed",true)) {
            repository.convertLeads(id, type, "completed", converted_to_job)
        }
        else
        {
            repository.convertLeads(id, type, status, converted_to_job)
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