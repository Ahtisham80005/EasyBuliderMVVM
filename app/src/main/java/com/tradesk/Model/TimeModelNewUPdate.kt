package com.tradesk.Model

data class TimeModelNewUPdate(
    val `data`: DataTimeModelNew,
    val message: String,
    val status: Int
)

data class DataTimeModelNew(
    val clockedInJob: List<ClockedInJob>? = null,
    val jobsData: List<JobsData>,
    val limit: Int,
    val page: Int,
    val totalPages: Int,
    val total_time: TotalTimeTimeModelNew
)

data class ClockedInJob(
    val _id: String,
    val clockedIn_address: ClockedInAddress,
    val clockedOut_address: ClockedOutAddress,
    val convertedSeconds: Int,
    val end_date: Any,
    val job_id: String,
    val start_date: String,
    val status: String,
    val timezone: String,
    val total_time: String
)

data class JobsData(
    val JobSheettotalTime: String,
    val _id: String,
    val additional_images: List<Any>,
    val address: AddressTimeModelNew,
    val client_data: List<ClientData>,
    val created_by: String,
    val description: String,
    val image: String,
    val project_name: String,
    val sales_data: List<SalesData>,
    val source: String,
    val status: String,
    val type: String
)

data class TotalTimeTimeModelNew(
    val _id: Any,
    val totalLogTime: String,
    val totalclockedIn: String,
    val totalclockedOut: String
)

data class ClockedInAddress(
    val city: String,
    val location: LocationTimeModelNew,
    val state: String,
    val street: String,
    val zipcode: String
)

data class ClockedOutAddress(
    val city: String,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationTimeModelNew(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class AddressTimeModelNew(
    val city: String,
    val location: LocationXTimeModelNew,
    val state: String,
    val street: String,
    val zipcode: String
)

data class ClientData(
    val _id: String,
    val name: String,
    val phone_no: String,
    val type: String
)

data class SalesData(
    val _id: String,
    val name: String,
    val phone_no: String,
    val type: String
)

data class LocationXTimeModelNew(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)