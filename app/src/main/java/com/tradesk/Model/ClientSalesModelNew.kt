package com.tradesk.Model

data class ClientSalesModelNew(
    val `data`: DataClientSales,
    val message: String,
    val status: Int
)

data class DataClientSales(
    val client: ClientClientSales,
    val job_details: JobDetails,
    val leads_details: LeadsDetails,
    val limit: Int,
    val page: Int
)

data class ClientClientSales(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressClientSales,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val home_phone_number: String,
    val image: String,
    val name: String,
    val phone_no: String,
    val privatenotes: String,
    val trade: String,
    val type: String,
    val updatedAt: String
)

data class JobDetails(
    val jobs: List<Job>,
    val totalPages: Int
)

data class LeadsDetails(
    val leads: List<Lead>,
    val totalPages: Int
)

data class AddressClientSales(
    val city: String,
    val location: LocationClientSales,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationClientSales(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class Job(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImageClientSales>,
    val address: AddressXClientSales,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val end_date: String,
    val image: String,
    val project_name: String,
    val source: String,
    val start_date: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val users_assigned: List<UsersAssignedClientSales>
)

data class AdditionalImageClientSales(
    val _id: String,
    val image: String
)

data class AddressXClientSales(
    val city: String,
    val location: LocationXClientSales,
    val state: String,
    val street: String,
    val zipcode: String
)

data class UsersAssignedClientSales(
    val _id: String,
    val user_id: String
)

data class LocationXClientSales(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)

data class Lead(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<Any>,
    val address: AddressXXClientSales,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val end_date: String,
    val image: String,
    val project_name: String,
    val source: String,
    val start_date: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val users_assigned: List<UsersAssignedX>
)

data class AddressXXClientSales(
    val city: String,
    val location: LocationXXClientSales,
    val state: String,
    val street: String,
    val zipcode: String
)

data class UsersAssignedX(
    val _id: String,
    val user_id: String
)

data class LocationXXClientSales(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)