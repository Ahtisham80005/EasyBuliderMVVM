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
class ProposalsViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responsePorposalsListModel: LiveData<NetworkResult<PorposalsListModel>>
        get()=repository.responsePorposalsListModel

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel

    val responseDeleteSelectedProposalSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseDeleteSelectedProposalSuccessModel

    val responseDeleteAllProposalSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseDeleteAllProposalSuccessModel

    val responseDeleteProposalSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseDeleteProposalSuccessModel

    val responseProposalDetailModel: LiveData<NetworkResult<ProposalDetailModel>>
        get()=repository.responseProposalDetailModel

    val responseAddProposalsModel: LiveData<NetworkResult<AddProposalsModel>>
        get()=repository.responseAddProposalsModel



    val responseUpdateProposalsModel: LiveData<NetworkResult<AddProposalsModel>>
        get()=repository.responseUpdateProposalsModel

    val responseDefaultItemsModel: LiveData<NetworkResult<DefaultItemsModel>>
        get()=repository.responseDefaultItemsModel

    val responseProfileModel: LiveData<NetworkResult<ProfileModel>>
        get()=repository.responseProfileModel

    val responseSendPorposals: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSendPorposals


    suspend fun getProposals(page: String, limit: String, status: String, type: String, id: String) {
        viewModelScope.launch {
            repository.getProposalsDetail(page, limit, status, type, id)
        }
    }
    suspend fun sendProposal(id: String, email: String) {
        viewModelScope.launch {
            repository.sendProposal(id, email)
        }
    }

    suspend fun deleteSelectedProposal(ids: SelectedIds) {
        viewModelScope.launch {
            repository.deleteSelectedProposal(ids)
        }
    }

    suspend fun getDetail(id: String) {
        viewModelScope.launch {
            repository.getDetail(id)
        }
    }

    suspend fun deleteproposal(id: String) {
        viewModelScope.launch {
            repository.deleteproposal(id)
        }
    }

    suspend fun deleteAllProposal(type: String,status: String) {
        viewModelScope.launch {
            repository.deleteAllProposal(type,status)
        }
    }

    suspend fun addProposals(map: HashMap<String, RequestBody>) {
        viewModelScope.launch {
            repository.addProposals(map)
        }
    }
    fun addProposals(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody>?,images:ArrayList<MultipartBody.Part>)
    {
        viewModelScope.launch {
            repository.addProposals(map,list,images)
        }
    }

    suspend fun updateProposal(map: HashMap<String, RequestBody>) {
        viewModelScope.launch {
            repository.updateProposal(map)
        }
    }
    suspend fun updateProposal(map: HashMap<String, RequestBody>,list: ArrayList<RequestBody>?,images:ArrayList<MultipartBody.Part>,existingDocs: ArrayList<RequestBody>?) {
        viewModelScope.launch {
            repository.updateProposal(map,list,images,existingDocs,)
        }
    }

    suspend fun getProposalItemslist()
    {
        viewModelScope.launch {
            repository.getProposalItemslist()
        }
    }

    suspend fun getProfile()
    {
        viewModelScope.launch {
            repository.getProfile()
        }
    }

}