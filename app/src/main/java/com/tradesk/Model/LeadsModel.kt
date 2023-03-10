package com.tradesk.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LeadsModel(
    val `data`: DataLeads,
    val message: String,
    val status: Int
) : Parcelable

@Parcelize
@Serializable
data class DataLeads(
    val leadsData: List<LeadsData>,
    val limit: Int,
    val page: Int,
    val countPendingLeads: Int,
    val countongoingLeads: Int,
    val countcompletedLeads: Int,
    val counttotalLeads: Int,
    val countdeleteLeads: Int,
    val countFollowUpLeads: Int,
    val totalPages: Int
) : Parcelable

@Parcelize
@Serializable
data class LeadsData(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressLeads,
    val client: List<ClientLeads>,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val description: String,
    val end_date: String,
    val image: String,
    val project_name: String,
    val sales: List<SaleLeads>,
    val source: String,
    val start_date: String,
    val status: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
@Serializable
data class AddressLeads(
    val city: String,
    val location: LocationLeads,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
@Serializable
data class ClientLeads(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressX,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val home_phone_number: String,
    val image: String,
    val name: String,
    val phone_no: String,
    val privatenotes: String,
    val trade: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
@Serializable
data class SaleLeads(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXX,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val home_phone_number: String,
    val image: String,
    val name: String,
    val phone_no: String,
    val privatenotes: String,
    val trade: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
@Serializable
data class LocationLeads(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable

@Parcelize
@Serializable
data class AddressX(
    val city: String,
    val location: LocationX,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
@Serializable
data class LocationX(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable

@Parcelize
@Serializable
data class AddressXX(
    val city: String,
    val location: LocationXX,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
@Serializable
data class LocationXX(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable