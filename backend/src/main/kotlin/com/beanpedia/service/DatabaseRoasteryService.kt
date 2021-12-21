package com.beanpedia.service

import com.beanpedia.exceptions.NotFoundException
import com.beanpedia.model.Roastery
import com.beanpedia.model.NewRoastery
import com.beanpedia.model.RoasteryEntities
import com.beanpedia.model.AddressEntities
import com.beanpedia.model.CountryEntities
import com.beanpedia.model.Address
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

interface RoasteryService {
    fun getAllRoasteries(): List<Roastery>
    fun getRoastery(id: UUID): Roastery?
    fun createRoastery(roastery: NewRoastery): Roastery
    fun deleteRoastery(id: UUID)
    fun updateRoastery(updatedRoastery: NewRoastery, id: UUID): Roastery
}

class DatabaseRoasteryService : RoasteryService {
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

    override fun getAllRoasteries(): List<Roastery> =
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

    override fun getRoastery(id: UUID): Roastery? = transaction {
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

    override fun createRoastery(roastery: NewRoastery): Roastery = transaction {
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
        val roasteryId = RoasteryEntities.insert {
            it[externalId] = UUID.randomUUID()
            it[name] = roastery.name
            it[description] = roastery.description
            it[addressId] = address
            it[phoneNumber] = roastery.phoneNumber
            it[website] = roastery.website
            it[facebook] = roastery.facebook
            it[instagram] = roastery.instagram
            it[twitter] = roastery.twitter
        }.resultedValues!!.map { it[RoasteryEntities.externalId] }.single()
        RoasteryEntities.join(
            AddressEntities, JoinType.LEFT,
            additionalConstraint = {
                AddressEntities.id eq RoasteryEntities.addressId
            }
        ).join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq AddressEntities.countryId }
        ).select {
            RoasteryEntities.externalId eq roasteryId
        }.map { toRoastery(it) }.single()
    }

    override fun deleteRoastery(id: UUID): Unit = transaction {
        val roastery = RoasteryEntities.select { RoasteryEntities.externalId eq id }.singleOrNull()
        if (roastery == null) {
            throw NotFoundException()
        } else {
            RoasteryEntities.deleteWhere { RoasteryEntities.externalId eq id }
        }
    }

    @Suppress("LongMethod")
    override fun updateRoastery(updatedRoastery: NewRoastery, id: UUID): Roastery = transaction {
        val roastery = RoasteryEntities.join(
            AddressEntities, JoinType.LEFT,
            additionalConstraint = {
                AddressEntities.id eq RoasteryEntities.addressId
            }
        ).join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq AddressEntities.countryId }
        ).select {
            RoasteryEntities.externalId eq id
        }.singleOrNull() ?: throw NotFoundException()

        var existingAddressId = roastery[RoasteryEntities.addressId]
        if (existingAddressId != null) {
            if (updatedRoastery.address == null) {
                AddressEntities.deleteWhere { AddressEntities.id eq existingAddressId!! }
                existingAddressId = null
            } else {
                AddressEntities.update({ AddressEntities.id eq existingAddressId!! }) {
                    it[address1] = updatedRoastery.address.address1
                    it[address2] = updatedRoastery.address.address2
                    it[address3] = updatedRoastery.address.address3
                    it[city] = updatedRoastery.address.city
                    it[postalCode] = updatedRoastery.address.city
                    it[countryId] = CountryEntities.slice(CountryEntities.id).select {
                        CountryEntities.alpha2Code eq updatedRoastery.address.country
                    }.single()[CountryEntities.id]
                }
            }
        } else if (updatedRoastery.address != null) {
            existingAddressId = AddressEntities.insert {
                it[address1] = updatedRoastery.address.address1
                it[address2] = updatedRoastery.address.address2
                it[address3] = updatedRoastery.address.address3
                it[city] = updatedRoastery.address.city
                it[postalCode] = updatedRoastery.address.postalCode
                it[countryId] = CountryEntities.slice(CountryEntities.id).select {
                    CountryEntities.alpha2Code eq updatedRoastery.address.country
                }.single()[CountryEntities.id]
            } get AddressEntities.id
        }

        RoasteryEntities.update({ RoasteryEntities.externalId eq id }) {
            it[name] = updatedRoastery.name
            it[description] = updatedRoastery.description
            it[addressId] = existingAddressId
            it[phoneNumber] = updatedRoastery.phoneNumber
            it[website] = updatedRoastery.website
            it[facebook] = updatedRoastery.facebook
            it[instagram] = updatedRoastery.instagram
            it[twitter] = updatedRoastery.twitter
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
