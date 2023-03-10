package com.tradesk.Model

data class PorposalsListModel(
    val `data`: DataPorposal,
    val message: String,
    val status: Int
)

data class DataPorposal(
    val limit: Int,
    val page: Int,
    val proposal_list: List<Proposal>,
    val totalPages: Int
)

data class Proposal(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val client_id: ClientIdPorposal,
    val client_signature: String,
    val contract_id: Any,
    val createdAt: String,
    val date: String,
    val deleted: Boolean,
    val mail_sent: Boolean,
    val estimate: String,
    val extra_info: String,
    val images: List<String>,
    val invoice_url: String,
    val items: List<Item>,
    val status: String,
    val subtotal: String,
    val tax: String,
    val type: String,
    val total: String,
    val updatedAt: String
)

data class ClientIdPorposal(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressPorposal,
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

data class Item(
    val _id: String,
    val amount: String,
    val description: String,
    val quantity: String,
    val title: String
)

data class AddressPorposal(
    val city: String,
    val location: LocationPorposal,
    val state: String,
    val street: String,
    val zipcode: String
)

data class LocationPorposal(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)