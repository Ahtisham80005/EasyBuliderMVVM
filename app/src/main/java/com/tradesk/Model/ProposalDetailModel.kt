package com.tradesk.Model

import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProposalDetailModel(
    val `data`: DataDetails,
    val message: String,
    val status: Int
)
@Serializable
data class DataDetails(
    val proposal_details: ProposalDetails
)
@Serializable
data class ProposalDetails(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val client_id: ClientIdProposalDetailModel,
    val client_signature: String,
    val my_signature: String,
    val contract_id: ContractId,
    val createdAt: String,
    val date: String,
    val deleted: Boolean,
    val estimate: String,
    val extra_info: String,
    val document: List<String>,
    val doc_url: List<String>,
    val isUrl: Boolean,
    val images: List<String>,
    val invoice_url: String,
    val items: List<ItemProposalDetailModel>,
    val status: String,
    val type: String,
    val subtotal: String,
    val tax: String,
    val tax_rate: String,
    val total: String,
    val updatedAt: String
)
@Serializable
data class ClientIdProposalDetailModel(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: AddressProposalDetailModel,
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
)
@Serializable
data class ItemProposalDetailModel(
    val _id: String,
    val amount: String,
    val description: String,
    val quantity: String,
    val title: String,
    val textAble: Boolean
)
@Serializable
data class AddressProposalDetailModel(
    val city: String,
    val location: LocationProposalDetailModel,
    val state: String,
    val street: String,
    val zipcode: String
)
@Serializable
data class LocationProposalDetailModel(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)
@Serializable
data class ContractId (

    @SerialName("_id") var Id : String,
    @SerialName("project_name") var projectName : String,
    @SerialName("description") var description : String,
    @SerialName("type") var type : String,
    @SerialName("status") var status : String,
    @SerialName("source") var source : String,
    @SerialName("created_by") var createdBy : String,
    @SerialName("image") var image : String,
    @SerialName("converted_to_job") var convertedToJob : Boolean,
    @SerialName("active") var active : Boolean,
    @SerialName("deleted") var deleted : Boolean,
    @SerialName("createdAt") var createdAt : String,
    @SerialName("updatedAt") var updatedAt : String,
    @SerialName("__v") var _v : Int

)