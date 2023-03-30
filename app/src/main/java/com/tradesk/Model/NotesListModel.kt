package com.tradesk.Model

data class NotesListModel(
    val `data`: DataNotes,
    val message: String,
    val status: Int
)

data class DataNotes(
    val limit: Int,
    val notes: List<Note>,
    val page: Int,
    val totalPages: Int
)

data class Note(
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