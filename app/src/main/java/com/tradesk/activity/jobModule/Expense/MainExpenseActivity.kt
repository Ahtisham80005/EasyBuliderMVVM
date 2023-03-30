package com.tradesk.activity.jobModule.Expense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tradesk.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainExpenseActivity : AppCompatActivity() {
    val fragment: ExpenseSingleFragment by lazy { ExpenseSingleFragment() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_expense)

        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = ExpenseSingleFragment()

        val mBundle = Bundle()
        mBundle.putString("job_id",intent.getStringExtra("job_id"))
        mFragment.arguments = mBundle
        mFragmentTransaction.add(R.id.mainContainer, mFragment).commit()
    }
}