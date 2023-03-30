package com.tradesk.Model

import kotlinx.serialization.Serializable

@Serializable
data class ExpensesListModel(
    val `data`: DataExpensesList,
    val message: String,
    val status: Int
)
@Serializable
data class DataExpensesList(
    val expenses_list: List<Expenses>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
)
@Serializable
data class Expenses(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val amount: String,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val image: String,
    val job_id: JobId,
    val title: String,
    val updatedAt: String
)
@Serializable
data class JobId(
    val __v: Int,
    val _id: String,
    val active: Boolean,

//    val additional_images: List<Any>,
    val address: AddressExpensesList,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val image: String,
    val project_name: String,
    val source: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val users_assigned: List<UsersAssigned>
)
@Serializable
data class AddressExpensesList(
    val city: String,
    val location: LocationExpensesList,
    val state: String,
    val street: String,
    val zipcode: String
)
@Serializable
data class UsersAssigned(
    val _id: String,
    val user_id: String
)
@Serializable
data class LocationExpensesList(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)