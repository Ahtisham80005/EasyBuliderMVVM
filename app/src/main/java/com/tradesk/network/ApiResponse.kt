package com.tradesk.network

import java.util.Objects

interface ApiResponse {
    fun onSuccessful(objects: Objects)
    fun onError(message:String)
}