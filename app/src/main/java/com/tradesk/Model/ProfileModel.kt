package com.tradesk.Model

data class ProfileModel(
    val `data`: DataProfile,
    val message: String,
    val status: Int
)

data class DataProfile(
    val __v: Int,
    val _id: String,
    val active: Boolean,
    val address: Address,
    val addtional_info: AddtionalInfo,
    val createdAt: String,
    val deleted: Boolean,
    val email: String,
    val image: String,
    val company_logo: String,
    val license_and_ins: LicenseAndIns,
    val name: String,
    val phone_no: String,
    val social_media: SocialMedia,
    val updatedAt: String,
    val userType: String,
    val company_name: String,
    val company_email: String
)

data class AddressProfile(
    val city: String,
    val country: String,
    val location: LocationProfile,
    val postal_code: String,
    val state: String,
    val street: String
)

data class AddtionalInfo(
    val email: String,
    val fax: String,
    val home_phone_no: String,
    val trade: String,
    val website_link: String
)

data class LicenseAndIns(
    val documents: String,
    val license: String,
    val licence_image: String,
    val docs_url: List<String>
)

data class SocialMedia(
    val facebook: String,
    val instagram: String,
    val googleBusiness: String,
    val yelp: String,
    val website: String
)

data class LocationProfile(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)