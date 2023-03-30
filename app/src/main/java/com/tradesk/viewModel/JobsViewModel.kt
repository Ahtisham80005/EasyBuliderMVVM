package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.*
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
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

    val responseTaskModel: LiveData<NetworkResult<TasksListModel>>
        get()=repository.responseTaskModel

    val responseTimesheetList: LiveData<NetworkResult<TimeModelNewUPdate>>
        get()=repository.responseTimesheetList

    val responseJobDetailTimesheet: LiveData<NetworkResult<NewTimeSheetModelClass>>
        get()=repository.responseJobDetailTimesheet

    val responseClockInOutModel: LiveData<NetworkResult<ClockInOutModel>>
        get()=repository.responseClockInOutModel

    val responseNotesListModel: LiveData<NetworkResult<NotesListModel>>
        get()=repository.responseNotesListModel

    val responseExpensesListModel: LiveData<NetworkResult<ExpensesListModel>>
        get()=repository.responseExpensesListModel

    val responsedeleteAllExpenseJobSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responsedeleteAllExpenseJobSuccessModel

    val responseAddExpenseModel: LiveData<NetworkResult<AddExpenseModel>>
        get()=repository.responseAddExpenseModel

    val responseMoveAdditionalImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseMoveAdditionalImagesSuccessModel

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
    suspend fun leadstaskslist(id: String)
    {
        viewModelScope.launch {
            repository.leadstaskslist(id)
        }
    }
    suspend fun addleadstasks(ids: String, titles: String, descriptions: String) {
        viewModelScope.launch {
            repository.addleadstasks(ids, titles, descriptions)
        }
    }
    suspend fun timesheetlist(page: String,limit:String,user_id:String) {
        viewModelScope.launch {
            repository.timesheetlist(page,limit,user_id)
        }
    }
    suspend fun jobsdetailtimesheet(id:String) {
        viewModelScope.launch {
            repository.jobsdetailtimesheet(id)
        }
    }
    suspend fun addtime(job_id: String,status:String,end_date: String,timezone: String,address:String,city: String,state:String,zipcode: String,latLong:String) {
        viewModelScope.launch {
            repository.addtime(job_id,status,end_date,timezone,address,city,state,zipcode,latLong)
        }
    }

    suspend fun leadsnoteslist(id: String) {
        viewModelScope.launch {
            repository.leadsnoteslist(id)
        }
    }
    suspend fun addleadsnotes(ids: String, titles: String, descriptions: String) {
        viewModelScope.launch {
            repository.addleadsnotes(ids, titles, descriptions)
        }
    }
    suspend fun getExpenseslist(page: String, limit: String, id: String) {
        viewModelScope.launch {
            repository.getExpenseslist(page, limit, id)
        }
    }
    suspend fun deleteSelectedExpense(selectedIds: SelectedIds) {
        viewModelScope.launch {
            repository.deleteSelectedExpense(selectedIds)
        }
    }
    suspend fun deleteAllExpenseJob(id: String) {
        viewModelScope.launch {
            repository.deleteAllExpenseJob(id)
        }
    }
    suspend fun updateExpense(id: String,map: HashMap<String, RequestBody>) {
        viewModelScope.launch {
            repository.updateExpense(id,map)
        }
    }
    suspend fun addexpense(map: HashMap<String, RequestBody>) {
        viewModelScope.launch {
            repository.addexpense(map)
        }
    }
    suspend fun addImgaes(map: HashMap<String, RequestBody>) {
        viewModelScope.launch {
            repository.addImgaes(map)
        }
    }

    suspend fun addMultipleImgaes(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>)
    {
        viewModelScope.launch {
            repository.addMultipleImgaes(map,images)
        }
    }
}