package com.beanpedia.service

import com.beanpedia.exceptions.NotFoundException
import com.beanpedia.helpers.DatabaseTest
import com.beanpedia.helpers.fakeNewBean
import com.beanpedia.helpers.fakeNewRoastery
import com.beanpedia.model.BeanEntities
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.NoSuchElementException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DatabaseRoasteryServiceTest : DatabaseTest() {
    private val roasteryService = DatabaseRoasteryService()
    private val beanService = DatabaseBeanService()

    @Test
    fun `get all roasteries`() {
        val newRoastery1 = fakeNewRoastery()
        val newRoastery2 = fakeNewRoastery()
        val createdRostery1 = roasteryService.createRoastery(newRoastery1)
        val createdRostery2 = roasteryService.createRoastery(newRoastery2)
        assertEquals(listOf(createdRostery1, createdRostery2), roasteryService.getAllRoasteries())
    }

    @Test
    fun `get all roasteries for empty database`() {
        assertEquals(emptyList(), roasteryService.getAllRoasteries())
    }

    @Test
    fun `get roastery for non existing roastery`() {
        assertNull(roasteryService.getRoastery(UUID.randomUUID()))
    }

    @Test
    fun `get roastery`() {
        val newRoastery1 = fakeNewRoastery()
        val newRoastery2 = fakeNewRoastery()
        val createdRostery1 = roasteryService.createRoastery(newRoastery1)
        roasteryService.createRoastery(newRoastery2)

        val requestedRoastery = roasteryService.getRoastery(UUID.fromString(createdRostery1.id))
        assertEquals(createdRostery1, requestedRoastery)
    }

    @Test
    fun `create roastery`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)
        assertEquals(newRoastery.name, createdRoastery.name)
    }

    @Test
    fun `create roastery for nonexisting country code`() {
        val newRoastery = fakeNewRoastery("XX")
        assertThrows<NoSuchElementException> { roasteryService.createRoastery(newRoastery) }
    }

    @Test
    fun `delete Roastery`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)
        roasteryService.deleteRoastery(UUID.fromString(createdRoastery.id))
        assertNull(roasteryService.getRoastery(UUID.fromString(createdRoastery.id)))
    }

    @Test
    fun `delete non existing roastery`() {
        assertThrows<NotFoundException> {
            roasteryService.deleteRoastery(UUID.randomUUID())
        }
    }

    @Test
    fun `delete roastery removes bean`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)
        val newBean = fakeNewBean(createdRoastery.id)
        beanService.createBean(newBean)

        roasteryService.deleteRoastery(UUID.fromString(createdRoastery.id))

        transaction {
            val beans = BeanEntities.selectAll().toList()
            assertEquals(emptyList(), beans)
        }
    }

    @Test
    fun `update roastery`() {
        val roastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(roastery)

        val newRoastery = fakeNewRoastery()
        val updatedRoastery = roasteryService.updateRoastery(newRoastery, UUID.fromString(createdRoastery.id))
        assertEquals(newRoastery.name, updatedRoastery.name)
        assertNotNull(newRoastery.address?.country)
        assertEquals(newRoastery.address?.country, updatedRoastery.address?.country)
    }

    @Test
    fun `update roastery non existing country code`() {
        val roastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(roastery)

        val newRoastery = fakeNewRoastery(countryCode = "XY")
        assertThrows<NoSuchElementException> {
            roasteryService.updateRoastery(newRoastery, UUID.fromString(createdRoastery.id))
        }
    }

    @Test
    fun `update roastery non existing roastery`() {
        val roastery = fakeNewRoastery()
        assertThrows<NotFoundException> {
            roasteryService.updateRoastery(roastery, UUID.randomUUID())
        }
    }
}
