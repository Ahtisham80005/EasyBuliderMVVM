package com.tradesk.Model

data class SignupModel(
    val `data`: DataSignup,
    val message: String,
    val status: Int
)

data class DataSignup(
    val address: Address,
    val email: String,
    val name: String
)

data class Address(
    val city: String,
    val country: String,
    val location: Location,
    val postal_code: String,
    val state: String,
    val street: String
)

data class Location(
    val _id: String,
    val coordinates: List<Double>,
    val type: String
)