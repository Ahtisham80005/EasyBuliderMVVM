package com.tradesk.Model

data class RemindersModel(
    val `data`: DataReminders,
    val message: String,
    val status: Int
)

data class DataReminders(
    val limit: Int,
    val page: Int,
    val remainderData: List<RemainderData>,
    val totalPages: Int
)

data class RemainderData(
    val __v: Int,
    val _id: String,
    val client_id: ClientId? = null,
    val createdAt: String,
    val dateTime: String,
    val description: String,
    val deleted: Boolean,
    val id: Id? = null,
    val remainder_type: String,
    val status: String,
    val type: String,
    val updatedAt: String
)

data class ClientId(
    val _id: String,
    val name: String
)

data class Id(
    val _id: String,
    val project_name: String
)