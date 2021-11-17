package com.beanpedia.service

import com.beanpedia.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class RoasteryService {
    private fun toRoastery(row: ResultRow): Roastery {
        val address = if (row[RoasteryEntities.addressId] != null) {
            Address(
                address1 = row[AddressEntities.address1],
                address2 = row[AddressEntities.address2],
                address3 = row[AddressEntities.address3],
                city = row[AddressEntities.city],
                postalCode = row[AddressEntities.postalCode],
                country = row[CountryEntities.alpha2Code]
            )
        } else null
        return Roastery(
            id = row[RoasteryEntities.externalId].toString(),
            name = row[RoasteryEntities.name],
            description = row[RoasteryEntities.description],
            address = address,
            phoneNumber = row[RoasteryEntities.phoneNumber],
            website = row[RoasteryEntities.website],
            facebook = row[RoasteryEntities.facebook],
            instagram = row[RoasteryEntities.instagram],
            twitter = row[RoasteryEntities.twitter],
        )
    }

    fun getAllRoasteries(): List<Roastery> =
        transaction {
            RoasteryEntities.join(
                AddressEntities, JoinType.LEFT,
                additionalConstraint = {
                    AddressEntities.id eq RoasteryEntities.addressId
                }
            ).join(
                CountryEntities, JoinType.LEFT,
                additionalConstraint = { CountryEntities.id eq AddressEntities.countryId }
            ).selectAll().map {
                it
            }.map { toRoastery(it) }
        }

    fun getRoastery(id: UUID): Roastery? = transaction {
        RoasteryEntities.join(
            AddressEntities, JoinType.LEFT,
            additionalConstraint = {
                AddressEntities.id eq RoasteryEntities.addressId
            }
        ).join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq AddressEntities.countryId }
        ).select {
            RoasteryEntities.externalId eq id
        }.mapNotNull { toRoastery(it) }.singleOrNull()
    }

    fun createRoastery(roastery: NewRoastery): Roastery = transaction {
        val address = if (roastery.address != null) {
            AddressEntities.insert {
                it[address1] = roastery.address.address1
                it[address2] = roastery.address.address2
                it[address3] = roastery.address.address3
                it[city] = roastery.address.city
                it[postalCode] = roastery.address.postalCode
                it[countryId] = CountryEntities.slice(CountryEntities.id).select {
                    CountryEntities.alpha2Code eq roastery.address.country
                }.single()[CountryEntities.id]
            } get AddressEntities.id
        } else null
        RoasteryEntities.insert {
            it[externalId] = UUID.randomUUID()
            it[name] = roastery.name
            it[description] = roastery.description
            it[addressId] = address
            it[phoneNumber] = roastery.phoneNumber
            it[website] = roastery.website
            it[facebook] = roastery.facebook
            it[instagram] = roastery.instagram
            it[twitter] = roastery.twitter
        }.resultedValues!!.map { toRoastery(it) }.single()
    }

    fun deleteRoastery(id: UUID): Unit = transaction {
        RoasteryEntities.deleteWhere { RoasteryEntities.externalId eq id }
    }

    fun updateRoastery(updatedRoastery: NewRoastery, id: UUID): Roastery = transaction {
        if (updatedRoastery.address != null) {
            RoasteryEntities.join(
                AddressEntities, JoinType.LEFT,
                additionalConstraint = {
                    AddressEntities.id eq RoasteryEntities.addressId
                }
            ).join(
                CountryEntities, JoinType.LEFT,
                additionalConstraint = { CountryEntities.id eq AddressEntities.countryId }
            ).update({ RoasteryEntities.externalId eq id }) {
                it[RoasteryEntities.name] = updatedRoastery.name
                it[RoasteryEntities.description] = updatedRoastery.description
                it[RoasteryEntities.addressId] = null
                it[RoasteryEntities.phoneNumber] = updatedRoastery.phoneNumber
                it[RoasteryEntities.website] = updatedRoastery.website
                it[RoasteryEntities.facebook] = updatedRoastery.facebook
                it[RoasteryEntities.instagram] = updatedRoastery.instagram
                it[RoasteryEntities.twitter] = updatedRoastery.twitter
                it[AddressEntities.address1] = updatedRoastery.address.address1
                it[AddressEntities.address2] = updatedRoastery.address.address2
                it[AddressEntities.address3] = updatedRoastery.address.address3
                it[AddressEntities.city] = updatedRoastery.address.city
                it[AddressEntities.postalCode] = updatedRoastery.address.postalCode
                it[AddressEntities.countryId] = CountryEntities.slice(CountryEntities.id).select {
                    CountryEntities.alpha2Code eq updatedRoastery.address.country
                }.single()[CountryEntities.id]
            }
        } else {
            RoasteryEntities.update({ RoasteryEntities.externalId eq id }) {
                it[name] = updatedRoastery.name
                it[description] = updatedRoastery.description
                it[addressId] = null
                it[phoneNumber] = updatedRoastery.phoneNumber
                it[website] = updatedRoastery.website
                it[facebook] = updatedRoastery.facebook
                it[instagram] = updatedRoastery.instagram
                it[twitter] = updatedRoastery.twitter
            }
        }
        RoasteryEntities.join(
            AddressEntities, JoinType.LEFT,
            additionalConstraint = {
                AddressEntities.id eq RoasteryEntities.addressId
            }
        ).join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq AddressEntities.countryId }
        ).select {
            RoasteryEntities.externalId eq id
        }.map { toRoastery(it) }.single()
    }
}
