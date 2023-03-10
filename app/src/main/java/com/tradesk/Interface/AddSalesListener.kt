package com.tradesk.Interface

import com.tradesk.Model.Client

interface AddSalesListener {
    fun onAddSalesClick(item: Client, position: Int)
}