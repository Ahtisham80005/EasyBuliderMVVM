package com.tradesk.Model

data class ClientsListModel(
    val `data`: DataClients,
    val message: String,
    val status: Int
)

data class DataClients(
    val client: List<Client>,
    val limit: Int,
    val page: Int,
    val totalPages: Int
)


data class Client(
    val __v: Int,
    var _id: String,
    val active: Boolean,
    val address: AddressClient,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val image: String,
    val name: String,
    val phone_no: String,
    val designation: String,
    val trade: String,
    val type: String,
    val updatedAt: String
)


data class AddressClient(
    val city: String,
    val location: LocationClients,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationClients(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)