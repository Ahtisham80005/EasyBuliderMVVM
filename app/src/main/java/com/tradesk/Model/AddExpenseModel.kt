package com.tradesk.Model

data class AddExpenseModel(
    val `data`: DataAddExpense,
    val message: String,
    val status: Int
)

data class DataAddExpense(
    val expenseData: ExpenseData
)

data class ExpenseData(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val amount: String,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val image: String,
    val job_id: String,
    val title: String,
    val updatedAt: String
)