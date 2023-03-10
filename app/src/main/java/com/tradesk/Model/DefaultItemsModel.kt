package com.tradesk.Model

data class DefaultItemsModel(
    val `data`: DataItems,
    val message: String,
    val status: Int
)

data class DataItems(
    val items_data: List<ItemsData>
)

data class ItemsData(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val amount: String,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val quantity: String,
    val title: String,
    val type: String,
    val updatedAt: String
)