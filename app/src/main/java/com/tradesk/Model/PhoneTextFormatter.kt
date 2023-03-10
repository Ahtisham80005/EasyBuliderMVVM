package com.tradesk.Model

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText


class PhoneTextFormatter(private val mEditText: EditText, private val mPattern: String) :
    TextWatcher {
    private val TAG = this.javaClass.simpleName
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val phone = StringBuilder(s)
        Log.d(TAG, "join")
        if (count > 0 && !isValid(phone.toString())) {
            for (i in 0 until phone.length) {
                Log.d(TAG, String.format("%s", phone))
                val c = mPattern[i]
                if (c != '#' && c != phone[i]) {
                    phone.insert(i, c)
                }
            }
            mEditText.setText(phone)
            mEditText.setSelection(mEditText.text.length)
        }
    }

    override fun afterTextChanged(s: Editable) {}
    private fun isValid(phone: String): Boolean {
        for (i in 0 until phone.length) {
            val c = mPattern[i]
            if (c == '#') continue
            if (c != phone[i]) {
                return false
            }
        }
        return true
    }

    init {
        //set max length of string
        val maxLength = mPattern.length
        mEditText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
    }
}