package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.ProfileModel
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val repository: Repository) : ViewModel(){
    val responseProfileModel: LiveData<NetworkResult<ProfileModel>>
        get()=repository.responseProfileModel
    val responseUpdateProfileModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUpdateProfileModel

    val responseEditProfileDoc: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseEditProfileDoc

    suspend fun getProfile()
    {
        viewModelScope.launch {
            repository.getProfile()
        }
    }

    suspend fun updateProfile(map: HashMap<String, RequestBody>) {
        viewModelScope.launch {
            repository.editProfile(map)
        }
    }

    suspend fun updateProfileDoc(doc:ArrayList<MultipartBody.Part>) {
        viewModelScope.launch {
            repository.editprofiledocs(doc)
        }
    }
}