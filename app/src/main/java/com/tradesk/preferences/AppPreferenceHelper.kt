package com.tradesk.preferences

import android.content.Context
import android.content.SharedPreferences
import com.tradesk.di.Qualifier.PreferenceInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferenceHelper @Inject constructor(
    @ApplicationContext context: Context,
    @PreferenceInfo private val prefFileName: String): PreferenceHelper {

    private val mPrefs: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override fun setKeyValue(key: String, value: String) {

        mPrefs.edit().putString(key, value).apply()
    }


    override fun getKeyValue(key: String): String {
        return mPrefs.getString(key, "")!!
    }

    override fun logoutUser() {
        mPrefs.edit().clear().apply()
        mPrefs.edit().remove(PreferenceConstants.USER_LOGGED_IN).apply()
    }

    override fun isUserLoggedIn(key: String): Boolean {
        return mPrefs.getBoolean(key, false)
    }

    override fun setUserLoggedIn(key: String, isLogin: Boolean) {
        mPrefs.edit().putBoolean(key, isLogin).apply()
    }

    override fun isGotToken(key: String): Boolean {
        return mPrefs.getBoolean(key, false)
    }

    override fun setGotToken(key: String, isLogin: Boolean) {
        mPrefs.edit().putBoolean(key, isLogin).apply()
    }

    override fun getFrequentSearch(key: String): Set<String> {
        return mPrefs.getStringSet(key, mutableSetOf<String>())!!
    }

    override fun setFrequentSearch(key: String, value: Set<String>) {
        mPrefs.edit().putStringSet(key, value).apply()
    }
}