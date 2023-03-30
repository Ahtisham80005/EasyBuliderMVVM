package com.tradesk.Model

data class ClockInOutModel(
    val `data`: DataTime,
    val message: String,
    val status: Int
)

data class DataTime(
    val create_jobs_logs: CreateJobsLogs,
    val log_time: String,
    val total_time: TotalTime
)

data class CreateJobsLogs(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressTime,
    val client_id: Any,
    val createdAt: String,
    val deleted: Boolean,
    val end_date: Any,
    val job_id: String,
    val start_date: String,
    val status: String,
    val total_time: String,
    val updatedAt: String,
    val user_id: String
)

data class TotalTime(
    val _id: Any,
    val totalLogTime: String,
    val totalclockedIn: String,
    val totalclockedOut: String
)

data class AddressTime(
    val city: String,
    val location: LocationTime,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationTime(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)