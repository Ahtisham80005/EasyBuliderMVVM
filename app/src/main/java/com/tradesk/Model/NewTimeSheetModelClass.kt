package com.tradesk.Model

data class NewTimeSheetModelClass(
    val `data`: DataNewTimeSheet,
    val message: String,
    val status: Int
)

data class DataNewTimeSheet(
    val job_details: List<JobDetailNewTimeSheet>
)

data class JobDetailNewTimeSheet(
    val JobSheettotalTime: String,
    val _id: String,
    val additional_images: List<AdditionalImageNewTimeSheet>,
    val address: AddressNewTimeSheet,
    val created_by: String,
    val description: String,
    val image: String,
    val job_log_time: List<JobLogTimeNewTimeSheet>,
    val project_name: String,
    val source: String,
    val status: String,
    val type: String
)

data class AdditionalImageNewTimeSheet(
    val _id: String,
    val image: String
)

data class AddressNewTimeSheet(
    val city: String,
    val location: LocationNewTimeSheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class JobLogTimeNewTimeSheet(
    val _id: String,
    val daily_time_log: List<DailyTimeLogNewTimeSheet>,
    val end_date: String,
    val job_id: String,
    val start_date: String,
    val status: String,
    val timezone: String,
    val total_time: String
)

data class LocationNewTimeSheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class DailyTimeLogNewTimeSheet(
    val _id: String,
    val clockedIn_address: ClockedInAddressNewTimeSheet,
    val clockedOut_address: ClockedOutAddressNewTimeSheet,
    val end_date: String,
    val log_time: String,
    val start_date: String
)

data class ClockedInAddressNewTimeSheet(
    val city: String,
    val location: LocationXNewTimeSheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class ClockedOutAddressNewTimeSheet(
    val city: String,
    val location: LocationXXNewTimeSheet,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationXNewTimeSheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class LocationXXNewTimeSheet(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)