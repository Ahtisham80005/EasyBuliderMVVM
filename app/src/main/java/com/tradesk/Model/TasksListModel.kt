package com.tradesk.Model

data class TasksListModel(
    val `data`: DataTasks,
    val message: String,
    val status: Int
)

data class DataTasks(
    val limit: Int,
    val task: List<Task>,
    val page: Int,
    val totalPages: Int
)

data class Task(
    val __v: Int,
    val _id: String,
    var active: Boolean,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val leads_id: String,
    val title: String,
    val updatedAt: String
)