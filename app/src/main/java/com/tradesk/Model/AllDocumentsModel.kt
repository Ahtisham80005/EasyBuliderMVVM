package com.tradesk.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllDocumentsModel(
    val message: String,
    val status: Int,
    val jobs: List<JobDocuments>,
    val users: Users
) : Parcelable

@Parcelize
data class JobDocuments(
    val _id: String,
    val additional_images: List<AdditionalImageDocuments>,
    val project_name: String,
    val users_assigned: List<UsersAssignedAlbumsDocs>
) : Parcelable

@Parcelize
data class AdditionalImageDocuments(
    val _id: String,
    val image: String,
    val status: String
) : Parcelable

@Parcelize
data class Users(
    val _id: String,
    val license_and_ins: LicenseAndInsDocuments,
    val additional_documents: List<AdditionalDocument>,
    val name: String
) : Parcelable

@Parcelize
data class LicenseAndInsDocuments(
    val docs_url: List<String>,
    val documents: String,
    val licence_image: String,
    val license: String
) : Parcelable

@Parcelize
data class AdditionalDocument(
    val _id: String,
    val documents: List<String>,
    val folder_name: String
) : Parcelable

@Parcelize
data class UsersAssignedAlbumsDocs(
    val user_id: UserIdAlbumsDocs
) : Parcelable

@Parcelize
data class UserIdAlbumsDocs(
    val _id: String,
    val name: String
) : Parcelable