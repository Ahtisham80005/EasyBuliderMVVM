package com.tradesk.preferences


interface PreferenceHelper {

    fun getFrequentSearch(key: String): Set<String>

    fun setFrequentSearch(key: String, value: Set<String>)

    fun getKeyValue(key: String): String

    fun setKeyValue(key: String, value: String)

    fun logoutUser()

    fun isUserLoggedIn(key: String): Boolean

    fun setUserLoggedIn(key: String, isLogin: Boolean)

    fun isGotToken(key: String): Boolean

    fun setGotToken(key: String, isLogin: Boolean)


}