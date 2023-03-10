package com.tradesk.Model

data class CalendarDetailModel(
    val `data`: DataCalendarDetail,
    val message: String,
    val status: Int
)

data class DataCalendarDetail(
    val remainderData: List<RemainderDataCalendarDetail>,
    val reminderCount: List<ReminderCount>
)

data class RemainderDataCalendarDetail(
    val _id: String,
    val dateTime: String,
    val description: String,
    val remainder_type: String,
    val status: String,
    val timezone: String
)

data class ReminderCount(
    val date: String,
    val total_reminders: Int
)