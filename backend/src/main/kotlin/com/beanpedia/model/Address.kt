package com.beanpedia.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val address1: String,
    val address2: String?,
    val address3: String?,
    val city: String,
    val postalCode: String,
    val country: String
)
