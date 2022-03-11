package com.beanpedia.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

const val PRECISION = 3
const val SCALE = 2

enum class DegreeOfRoasting {
    LIGHT, MEDIUM, DARK
}

object CountryEntities : Table() {
    val id = integer("id").autoIncrement()
    val alpha2Code = char("alpha2Code", length = 2).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

object BeanOriginEntities : Table() {
    val beanId = reference("beanId", BeanEntities.id, ReferenceOption.CASCADE, ReferenceOption.RESTRICT)
    val origin = reference("origin", CountryEntities.id, ReferenceOption.RESTRICT, ReferenceOption.RESTRICT)
    init {
        uniqueIndex("beanOriginIndex", beanId, origin)
    }
}

object BeanEntities : Table() {
    val id = integer("id").autoIncrement()
    val externalId = uuid("externalId").uniqueIndex()
    val name = varchar("name", length = 255)
    val roasteryId = reference("roasteryId", RoasteryEntities.id, ReferenceOption.CASCADE, ReferenceOption.RESTRICT)
    val altitude = varchar("altitude", length = 255).nullable()
    val degreeOfRoasting = customEnumeration(
        "degreeOfRoasting",
        "ENUM('LIGHT', 'MEDIUM', 'DARK')",
        { value -> DegreeOfRoasting.valueOf(value as String) },
        { it.name }
    ).nullable()
    val description = text("description").nullable()
    val isWashed = bool("isWashed").nullable()
    val isSemiWashed = bool("isSemiWashed").nullable()
    val isNatural = bool("isNatural").nullable()
    val arabicaFraction = decimal("arabicaFraction", PRECISION, SCALE).nullable()
    val robustaFraction = decimal("robustaFraction", PRECISION, SCALE).nullable()
    val containsArabica = bool("containsArabica").nullable()
    val containsRobusta = bool("containsRobusta").nullable()
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class BeanProcessing(
    val isWashed: Boolean,
    val isSemiWashed: Boolean,
    val isNatural: Boolean,
)

@Serializable
data class BeanComposition(
    val containsArabica: Boolean,
    val containsRobusta: Boolean,
    val arabicaFraction: Float?,
    val robustaFraction: Float?,
)

@Serializable
data class Bean(
    val id: String,
    val name: String,
    val roasteryId: String,
    val altitude: String?,
    val processing: BeanProcessing?,
    val composition: BeanComposition?,
    val origins: Set<String>?,
    val degreeOfRoasting: DegreeOfRoasting?,
    val description: String?
)

@Serializable
data class NewBean(
    val name: String,
    val roasteryId: String,
    val altitude: String? = null,
    val processing: BeanProcessing? = null,
    val composition: BeanComposition? = null,
    val origins: Set<String>? = null,
    val degreeOfRoasting: DegreeOfRoasting? = null,
    val description: String? = null
)

@Serializable
data class NewBeanWithoutRoasteryId(
    val name: String,
    val altitude: String? = null,
    val processing: BeanProcessing? = null,
    val composition: BeanComposition? = null,
    val origins: Set<String>? = null,
    val degreeOfRoasting: DegreeOfRoasting? = null,
    val description: String? = null
) {
    fun toNewBean(roasteryId: String): NewBean = NewBean(
        name = this.name,
        roasteryId,
        altitude = this.altitude,
        processing = this.processing,
        composition = this.composition,
        origins = this.origins,
        degreeOfRoasting = this.degreeOfRoasting,
        description = this.description
    )
}
