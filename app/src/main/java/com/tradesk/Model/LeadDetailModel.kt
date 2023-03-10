package com.tradesk.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class LeadDetailModel(
    val `data`: DataLeadDetail,
    val message: String,
    val status: Int
) : Parcelable

@Parcelize
@Serializable
data class DataLeadDetail(
    val leadsData: LeadsDataLeadDetail,
    val subUsers: List<SubUserLeadDetail>
) : Parcelable

@Parcelize
@Serializable
data class LeadsDataLeadDetail(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val additional_images: List<AdditionalImageLeadDetail>? = null,
    val address: AddressLeadDetail,
    val client: List<ClientLeadDetail>,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val converted_to_job: Boolean,
    val description: String,
    val endDate: String,
    val image: String,
    val project_name: String,
    val sales: List<Sale>,
    val source: String,
    val startDate: String? = null,
    val status: String,
    val type: String,
    val updatedAt: String
) : Parcelable

@Parcelize
@Serializable
data class SubUserLeadDetail(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXXX,
    val createdAt: String,
    val created_by: String,
    val deleted: Boolean,
    val email: String,
    val image: String,
    val job_id: String,
    val mobile_no: String,
    val name: String,
    val trade: String,
    val updatedAt: String
) : Parcelable

@Parcelize
@Serializable
data class AdditionalImageLeadDetail(
    val _id: String,
    val image: String,
    val status: String? = null
) : Parcelable

@Parcelize
@Serializable
data class AddressLeadDetail(
    val city: String,
    val location: LocationLeadDetail,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable

@Parcelize
@Serializable
data class ClientLeadDetail(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressXLeadDetail,
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
data class Sale(
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
data class LocationLeadDetail(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable

@Parcelize
@Serializable
data class AddressXLeadDetail(
    val city: String,
    val location: AddressXXLeadDetail,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable


@Parcelize
@Serializable
data class AddressXXLeadDetail(
    val city: String,
    val location: LocationXX,
    val state: String,
    val street: String,
    val zipcode: String
) : Parcelable


@Parcelize
@Serializable
data class AddressXXX(
    val city: String,
    val country: String,
    val location: LocationXXX,
    val postal_code: String,
    val state: String,
    val street: String
) : Parcelable

@Parcelize
@Serializable
data class LocationXXX(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
) : Parcelable