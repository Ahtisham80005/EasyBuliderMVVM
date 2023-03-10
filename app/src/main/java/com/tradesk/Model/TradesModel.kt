package com.tradesk.Model


data class TradesModel(
    val `data`: DataTrades,
    val message: String,
    val status: Int
)

data class DataTrades(
    val limit: Int,
    val page: Int,
    val totalPages: Int,
    val tradeData: List<String>
)


data class DataTradesOld(
    val name: String,
    var isChecked: Boolean = false
)