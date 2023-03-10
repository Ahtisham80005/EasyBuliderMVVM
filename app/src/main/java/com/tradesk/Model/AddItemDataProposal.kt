package com.tradesk.Model

data class AddItemDataProposal(
    var name: String,
    var desc: String,
    var qty: Int = 0,
    var cost: Int = 0,
    var total: Int = 0,
    var tax: Int = 0
)