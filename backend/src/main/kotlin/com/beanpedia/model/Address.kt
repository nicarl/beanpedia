package com.beanpedia.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object AddressEntities : Table() {
    val id = integer("id").autoIncrement()
    val address1 = varchar("address1", length = 255)
    val address2 = varchar("address2", length = 255).nullable()
    val address3 = varchar("address3", length = 255).nullable()
    val city = varchar("city", length = 255)
    val postalCode = varchar("postalCode", length = 255)
    val countryId = reference("countryId", CountryEntities.id, ReferenceOption.RESTRICT, ReferenceOption.RESTRICT)
    override val primaryKey = PrimaryKey(BeanEntities.id)
}

@Serializable
data class Address(
    val address1: String,
    val address2: String?,
    val address3: String?,
    val city: String,
    val postalCode: String,
    val country: String
)
