package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.*
import com.tradesk.data.entity.MoveImagesInFolderModel
import com.tradesk.data.entity.MoveImagesJobToProfileModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class GallaryViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responsAdditionalImages: LiveData<NetworkResult<AdditionalImagesWithClientModel>>
        get()=repository.responsAdditionalImages
    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseSuccessModel
    val responseMoveImagesJobToProfileSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseMoveImagesJobToProfileSuccessModel

    val responseMoveAdditionalImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseMoveAdditionalImagesSuccessModel

    val responseDeleteImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseDeleteImagesSuccessModel

    val responseMoveImagesInFolderSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseMoveImagesInFolderSuccessModel

    val responseUsersAddAdditionalImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUsersAddAdditionalImagesSuccessModel

    val responseUserDelSelectedImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUserDelSelectedImagesSuccessModel

    val responseUserMoveImagesUserToJobSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUserMoveImagesUserToJobSuccessModel

    val responseUserMoveImagesUserToUserSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseUserMoveImagesUserToUserSuccessModel

    val responseLeadDetailModel: LiveData<NetworkResult<LeadDetailModel>>
        get()=repository.responseLeadDetailModel


    suspend fun getLeadDetail(id:String)
    {
        repository.getLeadDetail(id)
    }

    suspend fun getadditionalimagesjobs(page: String, limit: String, status: String)
    {
        viewModelScope.launch {
            repository.getadditionalimagesjobs(page, limit, status)
        }
    }
    suspend fun usersAddAlbum(map: HashMap<String, RequestBody>, images:ArrayList<MultipartBody.Part>)
    {
        viewModelScope.launch {
            repository.usersAddAlbum(map,images)
        }
    }
    suspend fun addMultipleImgaes(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>)
    {
        viewModelScope.launch {
            repository.addMultipleImgaes(map,images)
        }
    }

    suspend fun usersMoveImagesJobToProfile(moveImagesJobToProfileModel: MoveImagesJobToProfileModel) {
        viewModelScope.launch {
            repository.usersMoveImagesJobToProfile(moveImagesJobToProfileModel)
        }
    }

    suspend fun moveAdditionalImages(moveAdditionalImagesModel: MoveAdditionalImagesModel) {
        viewModelScope.launch {
            repository.moveAdditionalImages(moveAdditionalImagesModel)
        }
    }
    suspend fun deleteSelectedGallery(selectedImageIds: SelectedImageIds)
    {
        viewModelScope.launch {
            repository.deleteSelectedGallery(selectedImageIds)
        }
    }

    suspend fun moveImagesInFolder(moveImagesInFolderModel: MoveImagesInFolderModel) {
        viewModelScope.launch {
            repository.moveImagesInFolder(moveImagesInFolderModel)
        }
    }

    suspend fun usersAddAdditionalImages(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        viewModelScope.launch {
            repository.usersAddAdditionalImages(map,images)
        }
    }

    suspend fun userDelSelectedImages(selectedUrlsUser: SelectedUrlsUser) {
        viewModelScope.launch {
            repository.userDelSelectedImages(selectedUrlsUser)
        }
    }

    suspend fun userMoveImagesUserToJob(moveImagesUserToJob: MoveImagesUserToJob) {
        viewModelScope.launch {
            repository.userMoveImagesUserToJob(moveImagesUserToJob)
        }
    }
    suspend fun userMoveImagesUserToUser(moveImagesUserToUser: MoveImagesUserToUser) {
        viewModelScope.launch {
            repository.userMoveImagesUserToUser(moveImagesUserToUser)
        }
    }


}