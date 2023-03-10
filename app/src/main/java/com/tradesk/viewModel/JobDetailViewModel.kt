package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel
    suspend fun Forget(email: String)
    {
        viewModelScope.launch {
            repository.Forget(email)
        }
    }
}