package com.beanpedia.service

import com.beanpedia.exceptions.NotFoundException
import com.beanpedia.model.Address
import com.beanpedia.model.CountryEntities
import com.beanpedia.model.NewRoastery
import com.beanpedia.model.Roastery
import com.beanpedia.model.RoasteryEntities
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface RoasteryService {
    fun getAllRoasteries(): List<Roastery>
    fun getRoastery(id: UUID): Roastery?
    fun createRoastery(roastery: NewRoastery): Roastery
    fun deleteRoastery(id: UUID)
    fun updateRoastery(updatedRoastery: NewRoastery, id: UUID): Roastery
}

class DatabaseRoasteryService : RoasteryService {
    private fun hasAddress(row: ResultRow): Boolean {
        return row[RoasteryEntities.address1] != null &&
            row[RoasteryEntities.city] != null &&
            row[RoasteryEntities.postalCode] != null &&
            row[RoasteryEntities.countryId] != null
    }

    private fun toRoastery(row: ResultRow): Roastery {
        val address = if (hasAddress(row)) {
            Address(
                address1 = row[RoasteryEntities.address1]!!,
                address2 = row[RoasteryEntities.address2],
                address3 = row[RoasteryEntities.address3],
                city = row[RoasteryEntities.city]!!,
                postalCode = row[RoasteryEntities.postalCode]!!,
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
                CountryEntities, JoinType.LEFT,
                additionalConstraint = { CountryEntities.id eq RoasteryEntities.countryId }
            ).selectAll().map {
                it
            }.map { toRoastery(it) }
        }

    override fun getRoastery(id: UUID): Roastery? = transaction {
        RoasteryEntities.join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq RoasteryEntities.countryId }
        ).select {
            RoasteryEntities.externalId eq id
        }.mapNotNull { toRoastery(it) }.singleOrNull()
    }

    override fun createRoastery(roastery: NewRoastery): Roastery = transaction {
        val countryIdFromDb = if (roastery.address?.country != null)
            CountryEntities.slice(CountryEntities.id).select {
                CountryEntities.alpha2Code eq roastery.address.country
            }.single().get(CountryEntities.id) else null

        val roasteryId = RoasteryEntities.insert {
            it[externalId] = UUID.randomUUID()
            it[name] = roastery.name
            it[description] = roastery.description
            it[address1] = roastery.address?.address1
            it[address2] = roastery.address?.address2
            it[address3] = roastery.address?.address3
            it[city] = roastery.address?.city
            it[postalCode] = roastery.address?.postalCode
            it[countryId] = countryIdFromDb
            it[phoneNumber] = roastery.phoneNumber
            it[website] = roastery.website
            it[facebook] = roastery.facebook
            it[instagram] = roastery.instagram
            it[twitter] = roastery.twitter
        }.resultedValues!!.map { it[RoasteryEntities.externalId] }.single()
        RoasteryEntities.join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq RoasteryEntities.countryId }
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

    override fun updateRoastery(updatedRoastery: NewRoastery, id: UUID): Roastery = transaction {
        RoasteryEntities.join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq RoasteryEntities.countryId }
        ).select {
            RoasteryEntities.externalId eq id
        }.singleOrNull() ?: throw NotFoundException()

        val countryIdFromDb = if (updatedRoastery.address?.country != null)
            CountryEntities.slice(CountryEntities.id).select {
                CountryEntities.alpha2Code eq updatedRoastery.address.country
            }.single().get(CountryEntities.id) else null

        RoasteryEntities.update({ RoasteryEntities.externalId eq id }) {
            it[name] = updatedRoastery.name
            it[description] = updatedRoastery.description
            it[address1] = updatedRoastery.address?.address1
            it[address2] = updatedRoastery.address?.address2
            it[address3] = updatedRoastery.address?.address3
            it[city] = updatedRoastery.address?.city
            it[postalCode] = updatedRoastery.address?.postalCode
            it[countryId] = countryIdFromDb
            it[phoneNumber] = updatedRoastery.phoneNumber
            it[website] = updatedRoastery.website
            it[facebook] = updatedRoastery.facebook
            it[instagram] = updatedRoastery.instagram
            it[twitter] = updatedRoastery.twitter
        }
        RoasteryEntities.join(
            CountryEntities, JoinType.LEFT,
            additionalConstraint = { CountryEntities.id eq RoasteryEntities.countryId }
        ).select {
            RoasteryEntities.externalId eq id
        }.map { toRoastery(it) }.single()
    }
}
