package com.beanpedia.service

import com.beanpedia.model.Bean
import com.beanpedia.model.BeanComposition
import com.beanpedia.model.BeanEntities
import com.beanpedia.model.BeanOriginEntities
import com.beanpedia.model.BeanProcessing
import com.beanpedia.model.CountryEntities
import com.beanpedia.model.NewBean
import com.beanpedia.model.RoasteryEntities
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class BeanService {
    private fun toBean(row: ResultRow, origins: List<String>?): Bean {
        val beanProcessing = if (row[BeanEntities.isWashed] == true || row[BeanEntities.isSemiWashed] == true || row[BeanEntities.isNatural] == true) {
            BeanProcessing(
                isNatural = row[BeanEntities.isNatural] ?: false,
                isSemiWashed = row[BeanEntities.isSemiWashed] ?: false,
                isWashed = row[BeanEntities.isWashed] ?: false
            )
        } else { null }
        val beanComposition = if (row[BeanEntities.containsArabica] == true || row[BeanEntities.containsRobusta] == true) {
            BeanComposition(
                containsArabica = row[BeanEntities.containsArabica] ?: false,
                containsRobusta = row[BeanEntities.containsRobusta] ?: false,
                arabicaFraction = row[BeanEntities.arabicaFraction]?.toFloat(),
                robustaFraction = row[BeanEntities.robustaFraction]?.toFloat(),
            )
        } else { null }
        return Bean(
            id = row[BeanEntities.externalId].toString(),
            name = row[BeanEntities.name],
            roasteryId = row[RoasteryEntities.externalId].toString(),
            altitude = row[BeanEntities.altitude],
            processing = beanProcessing,
            composition = beanComposition,
            origins = origins,
            degreeOfRoasting = row[BeanEntities.degreeOfRoasting],
            description = row[BeanEntities.description]
        )
    }

    fun getAllBeans(): List<Bean> = transaction {
        BeanEntities.join(
            RoasteryEntities, JoinType.INNER, additionalConstraint = {
                BeanEntities.roasteryId eq RoasteryEntities.id
            }
        ).selectAll().map { toBean(it, getOrigins(it[BeanEntities.id])) }
    }

    private fun getOrigins(beanId: Int): List<String>? {
        return BeanOriginEntities.join(
            CountryEntities, JoinType.INNER, additionalConstraint = {
                BeanOriginEntities.origin eq CountryEntities.id
            }
        ).slice(CountryEntities.alpha2Code).select { BeanOriginEntities.beanId eq beanId }.map { it[CountryEntities.alpha2Code] }.toList().ifEmpty { null }
    }

    fun getBean(id: UUID): Bean? = transaction {
        val beanId = BeanEntities.slice(BeanEntities.id).select { BeanEntities.externalId eq id }.mapNotNull { it[BeanEntities.id] }.singleOrNull()
            ?: return@transaction null
        BeanEntities.join(
            RoasteryEntities, JoinType.INNER, additionalConstraint = {
                BeanEntities.roasteryId eq RoasteryEntities.id
            }
        ).select { BeanEntities.id eq beanId }.map { toBean(it, getOrigins(beanId)) }.single()
    }

    fun createBean(bean: NewBean): Bean = transaction {
        val internalRoasteryId = RoasteryEntities.slice(RoasteryEntities.id).select { RoasteryEntities.externalId eq UUID.fromString(bean.roasteryId) }.mapNotNull { it[RoasteryEntities.id] }.single()

        val newBeanId = BeanEntities.insert {
            it[externalId] = UUID.randomUUID()
            it[name] = bean.name
            it[roasteryId] = internalRoasteryId
            it[altitude] = bean.altitude
            it[degreeOfRoasting] = bean.degreeOfRoasting
            it[description] = bean.description
            it[isWashed] = bean.processing?.isWashed
            it[isSemiWashed] = bean.processing?.isSemiWashed
            it[isNatural] = bean.processing?.isNatural
            it[arabicaFraction] = bean.composition?.arabicaFraction?.toBigDecimal()
            it[robustaFraction] = bean.composition?.robustaFraction?.toBigDecimal()
            it[containsArabica] = bean.composition?.containsArabica
            it[containsRobusta] = bean.composition?.containsRobusta
        }.resultedValues!!.map { it[BeanEntities.id] }.single()

        if (bean.origins != null && bean.origins.isNotEmpty()) {
            BeanOriginEntities.batchInsert(bean.origins) {
                origin ->
                this[BeanOriginEntities.beanId] = newBeanId
                this[BeanOriginEntities.origin] = CountryEntities.slice(CountryEntities.id).select {
                    CountryEntities.alpha2Code eq origin
                }.single()[CountryEntities.id]
            }
        }
        return@transaction BeanEntities.join(
            RoasteryEntities, JoinType.INNER, additionalConstraint = {
                BeanEntities.roasteryId eq RoasteryEntities.id
            }
        ).select { BeanEntities.id eq newBeanId }.map { toBean(it, getOrigins(newBeanId)) }.single()
    }
}
