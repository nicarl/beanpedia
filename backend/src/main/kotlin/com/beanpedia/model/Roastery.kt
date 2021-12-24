package com.beanpedia.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object RoasteryEntities : Table() {
    val id = integer("id").autoIncrement()
    val externalId = uuid("externalId").uniqueIndex()
    val name = varchar("name", length = 255)
    val description = text("description").nullable()
    val address1 = varchar("address1", length = 255).nullable()
    val address2 = varchar("address2", length = 255).nullable()
    val address3 = varchar("address3", length = 255).nullable()
    val city = varchar("city", length = 255).nullable()
    val postalCode = varchar("postalCode", length = 255).nullable()
    val countryId = reference(
        "countryId",
        CountryEntities.id,
        ReferenceOption.RESTRICT,
        ReferenceOption.RESTRICT
    ).nullable()
    val phoneNumber = varchar("phoneNumber", length = 255).nullable()
    val website = varchar("website", length = 255).nullable()
    val facebook = varchar("facebook", length = 255).nullable()
    val instagram = varchar("instagram", length = 255).nullable()
    val twitter = varchar("twitter", length = 255).nullable()
    override val primaryKey = PrimaryKey(BeanEntities.id)
}

@Serializable
data class Roastery(
    val id: String,
    val name: String,
    val description: String?,
    val address: Address?,
    val phoneNumber: String?,
    val website: String?,
    val facebook: String?,
    val instagram: String?,
    val twitter: String?
)

@Serializable
data class NewRoastery(
    val name: String,
    val description: String? = null,
    val address: Address? = null,
    val phoneNumber: String? = null,
    val website: String? = null,
    val facebook: String? = null,
    val instagram: String? = null,
    val twitter: String? = null
)
