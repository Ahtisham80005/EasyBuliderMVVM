package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.LeadDetailModel
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeadDetailViewModel  @Inject constructor(val repository: Repository) : ViewModel() {

    val responseLeadDetailModel: LiveData<NetworkResult<LeadDetailModel>>
        get()=repository.responseLeadDetailModel

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel

    val responseLeadConvertSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseLeadConvertSuccessModel

    val responseAddReminderSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseAddReminderSuccessModel

    suspend fun getLeadDetail(id:String)
    {
        repository.getLeadDetail(id)
    }
    suspend fun LeadDelete(id:String)
    {
        repository.DeleteLead(id)
    }
    suspend fun convertLeads(id: String, type: String, status: String, converted_to_job: String)
    {
        if (status.equals("complete", true)||status.equals("completed",true)) {
            repository.convertLeads(id, type, "completed", converted_to_job)
        }
        else
        {
            repository.convertLeads(id, type, status, converted_to_job)
        }
    }
    suspend fun addreminder(id: String, client_id: String, remainder_type: String, type: String, dateTime: String, timezone: String)
    {
        repository.addreminder(id, client_id, remainder_type, type, dateTime, timezone)
    }
}