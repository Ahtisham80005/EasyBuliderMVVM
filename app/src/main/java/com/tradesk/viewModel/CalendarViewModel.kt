package com.tradesk.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradesk.Model.CalendarDetailModel
import com.tradesk.Model.ClientsListModel
import com.tradesk.Model.RemindersModel
import com.tradesk.Model.SuccessModel
import com.tradesk.network.NetworkResult
import com.tradesk.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val responsCalendarDetailModel: LiveData<NetworkResult<CalendarDetailModel>>
        get()=repository.responsCalendarDetailModel

    val responseDeleteReminderSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responseDeleteReminderSuccessModel

    val responsGetRemindersModel: LiveData<NetworkResult<RemindersModel>>
        get()=repository.responsGetRemindersModel

    val responsAddRemindersSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responsAddRemindersSuccessModel

    val responsEditRemindersSuccessModel: LiveData<NetworkResult<SuccessModel>>
        get()=repository.responsEditRemindersSuccessModel

    suspend fun calendardetail(timezone: String, date: String, dateType: String) {
        repository.calendardetail(timezone, date, dateType)
    }

    suspend fun getremindersdate(date: String,page: String,limit:String,timezone:String)
    {
        viewModelScope.launch {
            repository.getremindersdateCal(date,page,limit,timezone)
        }
    }
    suspend fun addreminderCal(remainder_type: String,type: String, dateTime: String, description: String, timezone: String) {
        viewModelScope.launch {
            repository.addreminderCal( remainder_type, type, dateTime,description,timezone)
        }
    }

    suspend fun editreminder(id: String,remainder_type: String,type: String, dateTime: String, description: String, timezone: String) {
        viewModelScope.launch {
            repository.editreminderCal(id,remainder_type, type, dateTime,description,timezone)
        }
    }

    suspend fun deletereminder(id: String) {
        viewModelScope.launch {
            repository.deletereminderCal(id)
        }
    }
}