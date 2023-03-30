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
class DocumentViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseAllDocumentsModel: LiveData<NetworkResult<AllDocumentsModel>>
        get()=repository.responseAllDocumentsModel

    val responseUsersAddAdditionalDocsSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUsersAddAdditionalDocsSuccessModel

    val responseLeadDetailModel: LiveData<NetworkResult<LeadDetailModel>>
        get()=repository.responseLeadDetailModel

    val responseDeleteAllDocumentsJobsSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseDeleteAllDocumentsJobsSuccessModel

    val responseDeleteSelectedDocumentsJobsSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseDeleteSelectedDocumentsJobsSuccessModel

    val responseAddImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseAddImagesSuccessModel

    val responseUsersDelSelectedAdditionalDocs: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUsersDelSelectedAdditionalDocs

    val responseUsersUpdateAdditionalDocs: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUsersUpdateAdditionalDocs

    suspend fun getAllDocuments() {
        viewModelScope.launch {
            repository.getAllDocs()
        }
    }
    suspend fun usersAddAdditionalDocs(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        viewModelScope.launch {
            repository.usersAddAdditionalDocs(map,images)
        }
    }
    suspend fun getLeadDetail(id:String) {
        viewModelScope.launch {
            repository.getLeadDetail(id)
        }
    }
    suspend fun deleteAllDocumentsJobs(job_id: String) {
        viewModelScope.launch {
            repository.deleteAllDocumentsJobs(job_id)
        }
    }

    suspend fun deleteSelectedDocumentsJobs(selectedDocsIds: SelectedDocsIds) {
        viewModelScope.launch {
            repository.deleteSelectedDocumentsJobs(selectedDocsIds)
        }
    }
    suspend fun addImages(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        viewModelScope.launch {
            repository.addImages(map,images)
        }
    }
    suspend fun deleteUserSelectedAdditionalDocs(selectedUserDocsDel: SelectedUserDocsDel) {
        viewModelScope.launch {
            repository.deleteUserSelectedAdditionalDocs(selectedUserDocsDel)
        }
    }
    suspend fun usersUpdateAdditionalDocs(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        viewModelScope.launch {
            repository.usersUpdateAdditionalDocs(map,images)
        }
    }

}