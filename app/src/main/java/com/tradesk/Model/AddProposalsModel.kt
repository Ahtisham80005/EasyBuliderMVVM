package com.tradesk.Model

data class AddProposalsModel(
    val `data`: DataAdd,
    val message: String,
    val status: Int
)

data class DataAdd(
    val pdfLink: String,
    val _id: String
)