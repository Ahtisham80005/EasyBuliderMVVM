package com.tradesk.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.*
import com.tradesk.data.entity.MoveImagesInFolderModel
import com.tradesk.data.entity.MoveImagesJobToProfileModel
import com.tradesk.util.Constants
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class Repository @Inject constructor(val easyBuliderAPI: EasyBuliderAPI) {

    private val _responseLoginModel= MutableLiveData<NetworkResult<LoginModel>>()
    val responseLoginModel : LiveData<NetworkResult<LoginModel>>
        get()=_responseLoginModel

    private val _responseSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseSuccessModel

    private val _responsedeleteAllExpenseJobSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responsedeleteAllExpenseJobSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responsedeleteAllExpenseJobSuccessModel

    private val _responseDeleteReminderSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseDeleteReminderSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseDeleteReminderSuccessModel

    private val _responseMoveImagesJobToProfileSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseMoveImagesJobToProfileSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseMoveImagesJobToProfileSuccessModel

    private val _responseDeleteImagesSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseDeleteImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseDeleteImagesSuccessModel

    private val _responseMoveAdditionalImagesSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseMoveAdditionalImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseMoveAdditionalImagesSuccessModel

    private val _responseMoveImagesInFolderSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseMoveImagesInFolderSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseMoveImagesInFolderSuccessModel

    private val _responseUsersAddAdditionalImagesSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUsersAddAdditionalImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUsersAddAdditionalImagesSuccessModel

    private val _responseUserDelSelectedImagesSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUserDelSelectedImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUserDelSelectedImagesSuccessModel

    private val _responseUserMoveImagesUserToJobSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUserMoveImagesUserToJobSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUserMoveImagesUserToJobSuccessModel

    private val _responseUserMoveImagesUserToUserSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUserMoveImagesUserToUserSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUserMoveImagesUserToUserSuccessModel

    private val _responseSignupModel= MutableLiveData<NetworkResult<SignupModel>>()
    val responseSignupModel: LiveData<NetworkResult<SignupModel>>
        get()=_responseSignupModel

    private val _responseProfileModel= MutableLiveData<NetworkResult<ProfileModel>>()
    val responseProfileModel: LiveData<NetworkResult<ProfileModel>>
        get()=_responseProfileModel

    private val _responseUpdateProfileModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUpdateProfileModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUpdateProfileModel

    private val _responseEditProfileDoc= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseEditProfileDoc: LiveData<NetworkResult<SuccessModel>>
        get()=_responseEditProfileDoc

    private val _responseAllLeadsModel= MutableLiveData<NetworkResult<LeadsModel>>()
    val responseAllLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=_responseAllLeadsModel

    private val _responseTaskModel= MutableLiveData<NetworkResult<TasksListModel>>()
    val responseTaskModel: LiveData<NetworkResult<TasksListModel>>
        get()=_responseTaskModel

    private val _responseNotesListModel= MutableLiveData<NetworkResult<NotesListModel>>()
    val responseNotesListModel: LiveData<NetworkResult<NotesListModel>>
        get()=_responseNotesListModel

    private val _responseExpensesListModel= MutableLiveData<NetworkResult<ExpensesListModel>>()
    val responseExpensesListModel: LiveData<NetworkResult<ExpensesListModel>>
        get()=_responseExpensesListModel

    private val _responseAddExpenseModel= MutableLiveData<NetworkResult<AddExpenseModel>>()
    val responseAddExpenseModel: LiveData<NetworkResult<AddExpenseModel>>
        get()=_responseAddExpenseModel

    private val _responseTimesheetList= MutableLiveData<NetworkResult<TimeModelNewUPdate>>()
    val responseTimesheetList: LiveData<NetworkResult<TimeModelNewUPdate>>
        get()=_responseTimesheetList

    private val _responseJobDetailTimesheet= MutableLiveData<NetworkResult<NewTimeSheetModelClass>>()
    val responseJobDetailTimesheet: LiveData<NetworkResult<NewTimeSheetModelClass>>
        get()=_responseJobDetailTimesheet

    private val _responseClockInOutModel= MutableLiveData<NetworkResult<ClockInOutModel>>()
    val responseClockInOutModel: LiveData<NetworkResult<ClockInOutModel>>
        get()=_responseClockInOutModel

    private val _responsePendingLeadsModel= MutableLiveData<NetworkResult<LeadsModel>>()
    val responsePendingLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=_responsePendingLeadsModel

    private val _responseFollowUPLeadsModel= MutableLiveData<NetworkResult<LeadsModel>>()
    val responseFollowUPLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=_responseFollowUPLeadsModel

    private val _responseSaleLeadsModel= MutableLiveData<NetworkResult<LeadsModel>>()
    val responseSaleLeadsModel: LiveData<NetworkResult<LeadsModel>>
        get()=_responseSaleLeadsModel

    private val _responseLeadDetailModel= MutableLiveData<NetworkResult<LeadDetailModel>>()
    val responseLeadDetailModel: LiveData<NetworkResult<LeadDetailModel>>
        get()=_responseLeadDetailModel

    private val _responseLeadConvertSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseLeadConvertSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseLeadConvertSuccessModel

    private val _responseAddReminderSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseAddReminderSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseAddReminderSuccessModel

    private val _responseUserDetail= MutableLiveData<NetworkResult<ClientSalesModelNew>>()
    val responseUserDetail: LiveData<NetworkResult<ClientSalesModelNew>>
        get()=_responseUserDetail

    private val _responseClientUpdateSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseClientUpdateSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseClientUpdateSuccessModel

    private val _responsAllSalesPerson= MutableLiveData<NetworkResult<ClientsListModel>>()
    val responsAllSalesPerson: LiveData<NetworkResult<ClientsListModel>>
        get()=_responsAllSalesPerson

    private val _responsAllSalesList= MutableLiveData<NetworkResult<ClientsListModel>>()
    val responsAllSalesList: LiveData<NetworkResult<ClientsListModel>>
        get()=_responsAllSalesList


    private val _responsDeleteSelectedSales= MutableLiveData<NetworkResult<SuccessModel>>()
    val responsDeleteSelectedSales: LiveData<NetworkResult<SuccessModel>>
        get()=_responsDeleteSelectedSales

    private val _responsDeleteAllClient= MutableLiveData<NetworkResult<SuccessModel>>()
    val responsDeleteAllClient: LiveData<NetworkResult<SuccessModel>>
        get()=_responsDeleteAllClient

    private val _responsAdditionalImages= MutableLiveData<NetworkResult<AdditionalImagesWithClientModel>>()
    val responsAdditionalImages: LiveData<NetworkResult<AdditionalImagesWithClientModel>>
        get()=_responsAdditionalImages

    private val _responsCalendarDetailModel= MutableLiveData<NetworkResult<CalendarDetailModel>>()
    val responsCalendarDetailModel: LiveData<NetworkResult<CalendarDetailModel>>
        get()=_responsCalendarDetailModel

    private val _responsGetRemindersModel= MutableLiveData<NetworkResult<RemindersModel>>()
    val responsGetRemindersModel: LiveData<NetworkResult<RemindersModel>>
        get()=_responsGetRemindersModel

    private val _responsAddRemindersSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responsAddRemindersSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responsAddRemindersSuccessModel

    private val _responsEditRemindersSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responsEditRemindersSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responsEditRemindersSuccessModel

    private val _responseAllDocumentsModel= MutableLiveData<NetworkResult<AllDocumentsModel>>()
    val responseAllDocumentsModel: LiveData<NetworkResult<AllDocumentsModel>>
        get()=_responseAllDocumentsModel

    private val _responseUsersAddAdditionalDocsSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUsersAddAdditionalDocsSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUsersAddAdditionalDocsSuccessModel

    private val _responseDeleteAllDocumentsJobsSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseDeleteAllDocumentsJobsSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseDeleteAllDocumentsJobsSuccessModel

    private val _responseDeleteSelectedDocumentsJobsSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseDeleteSelectedDocumentsJobsSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseDeleteSelectedDocumentsJobsSuccessModel

    private val _responseAddImagesSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseAddImagesSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseAddImagesSuccessModel

    private val _responseUsersDelSelectedAdditionalDocs= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUsersDelSelectedAdditionalDocs: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUsersDelSelectedAdditionalDocs

    private val _responseDeleteSelectedProposalSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseDeleteSelectedProposalSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseDeleteSelectedProposalSuccessModel

    private val _responseUsersUpdateAdditionalDocs= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseUsersUpdateAdditionalDocs: LiveData<NetworkResult<SuccessModel>>
        get()=_responseUsersUpdateAdditionalDocs

    private val _responsePorposalsListModel= MutableLiveData<NetworkResult<PorposalsListModel>>()
    val responsePorposalsListModel: LiveData<NetworkResult<PorposalsListModel>>
        get()=_responsePorposalsListModel

    private val _responseSendPorposals= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseSendPorposals: LiveData<NetworkResult<SuccessModel>>
        get()=_responseSendPorposals

    private val _responseProposalDetailModel= MutableLiveData<NetworkResult<ProposalDetailModel>>()
    val responseProposalDetailModel: LiveData<NetworkResult<ProposalDetailModel>>
        get()=_responseProposalDetailModel

    private val _responseDeleteAllProposalSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseDeleteAllProposalSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseDeleteAllProposalSuccessModel

    private val _responseDeleteProposalSuccessModel= MutableLiveData<NetworkResult<SuccessModel>>()
    val responseDeleteProposalSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=_responseDeleteProposalSuccessModel

    private val _responseAddProposalsModel= MutableLiveData<NetworkResult<AddProposalsModel>>()
    val responseAddProposalsModel: LiveData<NetworkResult<AddProposalsModel>>
        get()=_responseAddProposalsModel

    private val _responseUpdateProposalsModel= MutableLiveData<NetworkResult<AddProposalsModel>>()
    val responseUpdateProposalsModel: LiveData<NetworkResult<AddProposalsModel>>
        get()=_responseUpdateProposalsModel

    private val _responseDefaultItemsModel= MutableLiveData<NetworkResult<DefaultItemsModel>>()
    val responseDefaultItemsModel: LiveData<NetworkResult<DefaultItemsModel>>
        get()=_responseDefaultItemsModel


    suspend fun Login(email: String, password: String, device_token: String, device_type: String)
    {
            var response = easyBuliderAPI.login(email, password, device_token, device_type)
            if(response.isSuccessful && response!=null)
            {
                _responseLoginModel.postValue(NetworkResult.Success(response.body()!!))
            }
            else if(response.errorBody()!=null)
            {
                val errorStr = response.errorBody()!!.string()
                val message = (JSONObject(errorStr).getString("message"))
                _responseLoginModel.postValue(NetworkResult.Error(message))
            }
            else
            {
                _responseLoginModel.postValue(NetworkResult.Error("Something went wrong 2"))
            }
    }

    suspend fun Forget(email: String)
    {
            var response = easyBuliderAPI.forgot(email)
            if(response.isSuccessful && response!=null)
            {
                _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
            }
            else if(response.errorBody()!=null)
            {
                val errorStr = response.errorBody()!!.string()
                val message = (JSONObject(errorStr).getString("message"))
                _responseSuccessModel.postValue(NetworkResult.Error(message))
            }
            else
            {
                _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
            }
    }

    suspend fun Signup(name:String,email:String,addres:String,password:String,userType:String,city:String,state:String,postal_code:String,latLong:String,trade:String)
    {
        var response = easyBuliderAPI.signup(name,email,addres,password,userType,city,state,postal_code,trade,latLong)
        if(response.isSuccessful && response!=null)
        {
            _responseSignupModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSignupModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSignupModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun socialLogin(address: String, accessToken: String, device_token: String, device_type: String, post_code: String,
                            city: String, state: String, latLong: String, loginType: String
    )
    {
        var response = easyBuliderAPI.socialLogin(address, accessToken, device_token, device_type, "1", city,
            state, post_code, "unknown", latLong)
        if(response.isSuccessful && response!=null)
        {
            _responseLoginModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
//            if()
//            Value Unauthorized of type java.lang.String cannot be converted to JSONObject
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseLoginModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseLoginModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getProfile() {
        var response = easyBuliderAPI.getProfile()
        if(response.isSuccessful && response!=null)
        {
            _responseProfileModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            try{
                val message = (JSONObject(errorStr).getString("message"))
                _responseAllLeadsModel.postValue(NetworkResult.Error(message))
            }
            catch(e:Exception)
            {
                _responseProfileModel.postValue(NetworkResult.Error(errorStr))
            }
        }
        else
        {
            _responseProfileModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun editProfile(map: HashMap<String, RequestBody>) {
        var response = easyBuliderAPI.editProfile(map)
        if(response.isSuccessful && response!=null)
        {
            _responseUpdateProfileModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            try{
                val message = (JSONObject(errorStr).getString("message"))
                _responseAllLeadsModel.postValue(NetworkResult.Error(message))
            }
            catch(e:Exception)
            {
                _responseUpdateProfileModel.postValue(NetworkResult.Error(errorStr))
            }
        }
        else
        {
            _responseUpdateProfileModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun editprofiledocs(doc:ArrayList<MultipartBody.Part>) {
        var response = easyBuliderAPI.editprofiledocs(doc)
        if(response.isSuccessful && response!=null)
        {
            _responseEditProfileDoc.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseEditProfileDoc.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseEditProfileDoc.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getAllLead(type:String,page: String, limit: String, status: String)
    {
        var response = easyBuliderAPI.getleads(type, page, limit, status)
        if(response.isSuccessful && response!=null)
        {
            _responseAllLeadsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            try{
                val message = (JSONObject(errorStr).getString("message"))
                _responseAllLeadsModel.postValue(NetworkResult.Error(message))
            }
            catch(e:Exception)
            {
                _responseAllLeadsModel.postValue(NetworkResult.Error(errorStr))
            }

        }
        else
        {
            _responseAllLeadsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getPendingLead(type:String,page: String, limit: String, status: String)
    {
        var response = easyBuliderAPI.getleads(type, page, limit, status)
        if(response.isSuccessful && response!=null)
        {
            _responsePendingLeadsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsePendingLeadsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsePendingLeadsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getFollowUPLead(type:String,page: String, limit: String, status: String)
    {
        var response = easyBuliderAPI.getleads(type, page, limit, status)
        if(response.isSuccessful && response!=null)
        {
            _responseFollowUPLeadsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseFollowUPLeadsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseFollowUPLeadsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getSaleLead(type:String,page: String, limit: String, status: String)
    {
        var response = easyBuliderAPI.getleads(type, page, limit, status)
        if(response.isSuccessful && response!=null)
        {
            _responseSaleLeadsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSaleLeadsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSaleLeadsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getLeadDetail(id:String)
    {
        var response = easyBuliderAPI.getLeadDetail(id)
        if(response.isSuccessful && response!=null)
        {
            _responseLeadDetailModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseLeadDetailModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseLeadDetailModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun DeleteLead(id:String)
    {
        var response = easyBuliderAPI.deleteLead(id)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun convertLeads(id: String, type: String, status: String, converted_to_job: String)
    {
        var response=easyBuliderAPI.convertleads(id, type, status, converted_to_job)

        if(response.isSuccessful && response!=null)
        {
            _responseLeadConvertSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseLeadConvertSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseLeadConvertSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addreminder(id: String, client_id: String, remainder_type: String, type: String, dateTime: String, timezone: String) {
        var response=easyBuliderAPI.addreminder(id, client_id, remainder_type, type, dateTime, timezone)

        if(response.isSuccessful && response!=null)
        {
            _responseAddReminderSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseAddReminderSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseAddReminderSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun AddLeads(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody?>?) {
        var response=easyBuliderAPI.addleads(map, list)

        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun updateLeads(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody?>?) {
        var response=easyBuliderAPI.updateLeads(map, list)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun leadstaskslist(id: String) {
        var response=easyBuliderAPI.getLeadTasks(id)
        if(response.isSuccessful && response!=null)
        {
            _responseTaskModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseTaskModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseTaskModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addleadstasks(ids: String, titles: String, descriptions: String) {
        var response=easyBuliderAPI.leadaddtasks(ids, titles, descriptions)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun timesheetlist(page: String,limit:String,user_id:String) {
        var response=easyBuliderAPI.timesheetlist(page,limit,user_id)
        if(response.isSuccessful && response!=null)
        {
            _responseTimesheetList.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseTimesheetList.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseTimesheetList.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun jobsdetailtimesheet(id:String) {
        var response=easyBuliderAPI.jobsdetailtimesheet( id)
        if(response.isSuccessful && response!=null)
        {
            _responseJobDetailTimesheet.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseJobDetailTimesheet.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseJobDetailTimesheet.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addtime(job_id: String,status:String,end_date: String,timezone: String,address:String,city: String,state:String,zipcode: String,latLong:String) {
        var response=easyBuliderAPI.intime(job_id,status,end_date,timezone,address,city,state,zipcode,latLong)
        if(response.isSuccessful && response!=null)
        {
            _responseClockInOutModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseClockInOutModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseClockInOutModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun leadsnoteslist(id: String) {
        var response=easyBuliderAPI.getLeadNotes(id)
        if(response.isSuccessful && response!=null)
        {
            _responseNotesListModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseNotesListModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseNotesListModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun addleadsnotes(ids: String, titles: String, descriptions: String) {
        var response=easyBuliderAPI.leadaddnotes(ids, titles, descriptions)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getExpenseslist(page: String, limit: String, id: String) {
        var response=easyBuliderAPI.expenseslist(page, limit, id)
        if(response.isSuccessful && response!=null)
        {
            _responseExpensesListModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseExpensesListModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseExpensesListModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteSelectedExpense(selectedIds: SelectedIds) {
        var response=easyBuliderAPI.deleteSelectedExpense(selectedIds)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun deleteAllExpenseJob(id: String) {
        var response=easyBuliderAPI.deleteAllExpenseJob(id)
        if(response.isSuccessful && response!=null)
        {
            _responsedeleteAllExpenseJobSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsedeleteAllExpenseJobSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsedeleteAllExpenseJobSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun updateExpense(id: String,map: HashMap<String, RequestBody>) {
        var response=easyBuliderAPI.updateExpense(id,map)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun addexpense(map: HashMap<String, RequestBody>) {
        var response=easyBuliderAPI.addexpense(map)
        if(response.isSuccessful && response!=null)
        {
            _responseAddExpenseModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseAddExpenseModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseAddExpenseModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addImgaes(map: HashMap<String, RequestBody>) {
        var response=easyBuliderAPI.add_addtional_images(map)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }


    suspend fun getUserDetails(id: String, page: String, limit: String, type: String, status: String) {

        var response=easyBuliderAPI.clientdetails(id, page, limit, type, status)
        if(response.isSuccessful && response!=null)
        {
            _responseUserDetail.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUserDetail.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUserDetail.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getClientDetail(id: String, page: String, limit: String, type: String, status: String) {

        var response=easyBuliderAPI.clientdetails(id, page, limit, type, status)
        if(response.isSuccessful && response!=null)
        {
            _responseUserDetail.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUserDetail.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUserDetail.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getAllSalesPerson(type:String,page: String, limit: String) {

        var response=easyBuliderAPI.clientslist(type, page, limit)
        if(response.isSuccessful && response!=null)
        {
            _responsAllSalesPerson.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsAllSalesPerson.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsAllSalesPerson.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getAllClients(type:String,page: String, limit: String) {

        var response=easyBuliderAPI.clientslist(type, page, limit)
        if(response.isSuccessful && response!=null)
        {
            _responsAllSalesPerson.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsAllSalesPerson.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsAllSalesPerson.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun getAllSales(type: String,page: String, limit: String, trade: String) {
        var response=easyBuliderAPI.saleslist(type, page, limit,trade)
        if(response.isSuccessful && response!=null)
        {
            _responsAllSalesPerson.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsAllSalesPerson.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsAllSalesPerson.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addClient(map:HashMap<String, RequestBody>) {
        var response=easyBuliderAPI.addclient(map)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun updatesaleclient(map:HashMap<String, RequestBody>) {
        var response=easyBuliderAPI.updatesaleclient(map)
        if(response.isSuccessful && response!=null)
        {
            _responseClientUpdateSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseClientUpdateSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseClientUpdateSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteSelectedSales(selectedIds: SelectedIds) {
        var response=easyBuliderAPI.deleteSelectedClient(selectedIds)
        if(response.isSuccessful && response!=null)
        {
            _responsDeleteSelectedSales.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsDeleteSelectedSales.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsDeleteSelectedSales.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteAllClient(type: String) {
        var response=easyBuliderAPI.deleteAllClient(type)
        if(response.isSuccessful && response!=null)
        {
            _responsDeleteAllClient.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsDeleteAllClient.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsDeleteAllClient.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }


    suspend fun addSalesPersonInJob(client: String, jobId: String) {

        var response=easyBuliderAPI.addjobsubusers(client, jobId)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getadditionalimagesjobs(page: String, limit: String, status: String) {
        var response=easyBuliderAPI.getadditionalimagesjobs(page, limit, status)
        if(response.isSuccessful && response!=null)
        {
            _responsAdditionalImages.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsAdditionalImages.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsAdditionalImages.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun usersAddAlbum(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        var response=easyBuliderAPI.usersAddAlbum(map,images)
        if(response.isSuccessful && response!=null)
        {
            _responseSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addMultipleImgaes(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        var response=easyBuliderAPI.add_multiple_addtional_images(map,images)
        if(response.isSuccessful && response!=null)
        {
            _responseMoveAdditionalImagesSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseMoveAdditionalImagesSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseMoveAdditionalImagesSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun usersMoveImagesJobToProfile(moveImagesJobToProfileModel: MoveImagesJobToProfileModel){
        var response=easyBuliderAPI.usersMoveImagesJobToProfile(moveImagesJobToProfileModel)
        if(response.isSuccessful && response!=null)
        {
            _responseMoveImagesJobToProfileSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseMoveImagesJobToProfileSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseMoveImagesJobToProfileSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun moveAdditionalImages(moveAdditionalImagesModel: MoveAdditionalImagesModel) {
        var response=easyBuliderAPI.moveAdditionalImages(moveAdditionalImagesModel)
        if(response.isSuccessful && response!=null)
        {
            _responseMoveImagesJobToProfileSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseMoveImagesJobToProfileSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseMoveImagesJobToProfileSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteSelectedGallery(selectedImageIds: SelectedImageIds)
    {
        var response=easyBuliderAPI.deleteSelectedGallery(selectedImageIds)
        if(response.isSuccessful && response!=null)
        {
            _responseDeleteImagesSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDeleteImagesSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDeleteImagesSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun moveImagesInFolder(moveImagesInFolderModel: MoveImagesInFolderModel) {
        var response=easyBuliderAPI. moveImagesInFolder(moveImagesInFolderModel)
        if(response.isSuccessful && response!=null)
        {
            _responseMoveImagesInFolderSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseMoveImagesInFolderSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseMoveImagesInFolderSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun usersAddAdditionalImages(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        var response=easyBuliderAPI.usersAddAdditionalImages(map,images)
        if(response.isSuccessful && response!=null)
        {
            _responseUsersAddAdditionalImagesSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUsersAddAdditionalImagesSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUsersAddAdditionalImagesSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun userDelSelectedImages(selectedUrlsUser: SelectedUrlsUser) {
        var response=easyBuliderAPI.userDelSelectedImages(selectedUrlsUser)
        if(response.isSuccessful && response!=null)
        {
            _responseUserDelSelectedImagesSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUserDelSelectedImagesSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUserDelSelectedImagesSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun userMoveImagesUserToJob(moveImagesUserToJob: MoveImagesUserToJob) {
        var response=easyBuliderAPI.userMoveImagesUserToJob(moveImagesUserToJob)
        if(response.isSuccessful && response!=null)
        {
            _responseUserMoveImagesUserToJobSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUserMoveImagesUserToJobSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUserMoveImagesUserToJobSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun userMoveImagesUserToUser(moveImagesUserToUser: MoveImagesUserToUser) {
        var response=easyBuliderAPI.userMoveImagesUserToUser(moveImagesUserToUser)
        if(response.isSuccessful && response!=null)
        {
            _responseUserMoveImagesUserToUserSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUserMoveImagesUserToUserSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUserMoveImagesUserToUserSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun calendardetail(timezone: String, date: String, dateType: String) {
        var response=easyBuliderAPI.calendardetail(timezone, date, dateType)
        if(response.isSuccessful && response!=null)
        {
            _responsCalendarDetailModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsCalendarDetailModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsCalendarDetailModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getremindersdateCal(date: String,page: String,limit:String,timezone:String) {
        var response=easyBuliderAPI.getremindersdate("date",date,page,limit,timezone)
        if(response.isSuccessful && response!=null)
        {
            _responsGetRemindersModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsGetRemindersModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsGetRemindersModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }


    suspend fun addreminderCal(remainder_type: String,type: String, dateTime: String, description: String, timezone: String) {
        var response=easyBuliderAPI.addreminderCal(remainder_type,type,dateTime,description,timezone)
        if(response.isSuccessful && response!=null)
        {
            _responsAddRemindersSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsAddRemindersSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsAddRemindersSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun editreminderCal(id: String,remainder_type: String,type: String, dateTime: String, description: String, timezone: String)
    {
        var response=easyBuliderAPI.editreminderCal(id,remainder_type, type, dateTime,description,timezone)
        if(response.isSuccessful && response!=null)
        {
            _responsEditRemindersSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsEditRemindersSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsEditRemindersSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun deletereminderCal(id: String) {
        var response=easyBuliderAPI.deletereminderCal(id)
        if(response.isSuccessful && response!=null)
        {
            _responseDeleteReminderSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDeleteReminderSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDeleteReminderSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getAllDocs(){
        var response=easyBuliderAPI.getAllDocs()
        if(response.isSuccessful && response!=null)
        {
            _responseAllDocumentsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseAllDocumentsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseAllDocumentsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun deleteAllDocumentsJobs(job_id: String) {
        var response=easyBuliderAPI.deleteAllDocumentsJobs(job_id)
        if(response.isSuccessful && response!=null)
        {
            _responseDeleteAllDocumentsJobsSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDeleteAllDocumentsJobsSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDeleteAllDocumentsJobsSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun usersAddAdditionalDocs(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        var response=easyBuliderAPI.usersAddAdditionalDocs(map,images)
        if(response.isSuccessful && response!=null)
        {
            _responseUsersAddAdditionalDocsSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUsersAddAdditionalDocsSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUsersAddAdditionalDocsSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteSelectedDocumentsJobs(selectedDocsIds: SelectedDocsIds) {
        var response=easyBuliderAPI.deleteSelectedDocumentsJobs(selectedDocsIds)
        if(response.isSuccessful && response!=null)
        {
            _responseDeleteSelectedDocumentsJobsSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDeleteSelectedDocumentsJobsSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDeleteSelectedDocumentsJobsSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun addImages(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        var response=easyBuliderAPI.add_multiple_addtional_images(map,images)
        if(response.isSuccessful && response!=null)
        {
            _responseAddImagesSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseAddImagesSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseAddImagesSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteUserSelectedAdditionalDocs(selectedUserDocsDel: SelectedUserDocsDel) {
        var response=easyBuliderAPI.usersDelSelectedAdditionalDocs(selectedUserDocsDel)
        if(response.isSuccessful && response!=null)
        {
            _responseUsersDelSelectedAdditionalDocs.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUsersDelSelectedAdditionalDocs.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUsersDelSelectedAdditionalDocs.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun usersUpdateAdditionalDocs(map: HashMap<String, RequestBody>,images:ArrayList<MultipartBody.Part>) {
        var response=easyBuliderAPI.usersUpdateAdditionalDocs(map,images)
        if(response.isSuccessful && response!=null)
        {
            _responseUsersUpdateAdditionalDocs.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUsersUpdateAdditionalDocs.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUsersUpdateAdditionalDocs.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getProposalsDetail(page: String, limit: String, status: String, type: String, id: String) {
        var response=easyBuliderAPI.getproposals(page, limit, status, type, id)
        if(response.isSuccessful && response!=null)
        {
            _responsePorposalsListModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responsePorposalsListModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responsePorposalsListModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }
    suspend fun sendProposal(id: String, email: String) {
        var response=easyBuliderAPI.sendproposal(id, email)
        if(response.isSuccessful && response!=null)
        {
            _responseSendPorposals.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseSendPorposals.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseSendPorposals.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteSelectedProposal(ids: SelectedIds) {
        var response=easyBuliderAPI.deleteSelectedProposals(ids)
        if(response.isSuccessful && response!=null)
        {
            _responseDeleteSelectedProposalSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDeleteSelectedProposalSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDeleteSelectedProposalSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getDetail(id: String) {
        var response=easyBuliderAPI.getProposalDetail(id)
        if(response.isSuccessful && response!=null)
        {
            _responseProposalDetailModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseProposalDetailModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseProposalDetailModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteAllProposal(type: String,status: String) {
        var response=easyBuliderAPI.deleteAllProposals(type, status)
        if(response.isSuccessful && response!=null)
        {
            _responseDeleteAllProposalSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDeleteAllProposalSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDeleteAllProposalSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun deleteproposal(id: String) {
        var response=easyBuliderAPI. deleteproposal(id)
        if(response.isSuccessful && response!=null)
        {
            _responseDeleteProposalSuccessModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDeleteProposalSuccessModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDeleteProposalSuccessModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addProposals(map: HashMap<String, RequestBody>) {
        var response=easyBuliderAPI. addproposal(map)
        if(response.isSuccessful && response!=null)
        {
            _responseAddProposalsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseAddProposalsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseAddProposalsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun addProposals(map: HashMap<String, RequestBody>, list: ArrayList<RequestBody>?,images:ArrayList<MultipartBody.Part>) {
        var response=easyBuliderAPI.addproposal(map,list,images)
        if(response.isSuccessful && response!=null)
        {
            _responseAddProposalsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseAddProposalsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseAddProposalsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }


    suspend fun updateProposal(map: HashMap<String, RequestBody>) {
        var response=easyBuliderAPI.updateProposal(map)
        if(response.isSuccessful && response!=null)
        {
            _responseUpdateProposalsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUpdateProposalsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUpdateProposalsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun updateProposal(map: HashMap<String, RequestBody>,list: ArrayList<RequestBody>?,images:ArrayList<MultipartBody.Part>,existingDocs: ArrayList<RequestBody>?) {
        var response=easyBuliderAPI.updateProposal(map,list,existingDocs,images)
        if(response.isSuccessful && response!=null)
        {
            _responseUpdateProposalsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseUpdateProposalsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseUpdateProposalsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }

    suspend fun getProposalItemslist() {
        var response=easyBuliderAPI.proposalItemslist()
        if(response.isSuccessful && response!=null)
        {
            _responseDefaultItemsModel.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null)
        {
            val errorStr = response.errorBody()!!.string()
            val message = (JSONObject(errorStr).getString("message"))
            _responseDefaultItemsModel.postValue(NetworkResult.Error(message))
        }
        else
        {
            _responseDefaultItemsModel.postValue(NetworkResult.Error("Something went wrong 2"))
        }
    }


    fun getErrorMessage(responseBody: okhttp3.ResponseBody?): String {
        try {
            val jsonObject = JSONObject(responseBody!!.string())
            if (jsonObject.has("error_description"))
                return jsonObject.getString("error_description")
            else
                return jsonObject.getString("message")
        } catch (e: Exception) {
            return e.message!!
        }
    }

}