package com.tradesk.Interface

import com.tradesk.Model.Client

interface AddClientListener {
    fun onAddClientClick(item: Client, position: Int)
}