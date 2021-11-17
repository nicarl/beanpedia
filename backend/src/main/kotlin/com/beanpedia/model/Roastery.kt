package com.beanpedia.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object RoasteryEntities : Table() {
    val id = integer("id").autoIncrement()
    val externalId = uuid("externalId").uniqueIndex()
    val name = varchar("name", length = 255)
    val description = text("description").nullable()
    val addressId = reference("addressId", AddressEntities.id, ReferenceOption.CASCADE, ReferenceOption.RESTRICT).nullable()
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
