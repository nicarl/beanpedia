package com.beanpedia.service

import com.beanpedia.exceptions.NotFoundException
import com.beanpedia.helpers.DatabaseTest
import com.beanpedia.helpers.fakeNewBean
import com.beanpedia.helpers.fakeNewRoastery
import com.beanpedia.model.BeanOriginEntities
import com.beanpedia.model.NewBean
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.NoSuchElementException
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatabaseBeanServiceTest : DatabaseTest() {
    private val roasteryService = DatabaseRoasteryService()
    private val beanService = DatabaseBeanService()

    @Test
    fun `get all beans for empty database`() {
        assertEquals(emptyList(), beanService.getAllBeans())
    }

    @Test
    fun `get all beans`() {
        val newRoastery1 = fakeNewRoastery()
        val newRoastery2 = fakeNewRoastery()
        val createdRostery1 = roasteryService.createRoastery(newRoastery1)
        val createdRostery2 = roasteryService.createRoastery(newRoastery2)
        val newBean1 = fakeNewBean(createdRostery1.id)
        val newBean2 = fakeNewBean(createdRostery2.id)

        val createdBean1 = beanService.createBean(newBean1)
        val createdBean2 = beanService.createBean(newBean2)

        assertEquals(listOf(createdBean1, createdBean2), beanService.getAllBeans())
    }

    @Test
    fun `create bean`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)

        val newBean = fakeNewBean(createdRoastery.id)

        val createdBean = beanService.createBean(newBean)
        assertEquals(createdBean.name, newBean.name)
    }

    @Test
    fun `create bean for invalid roastery fails`() {
        val newBean = fakeNewBean(UUID.randomUUID().toString())

        assertThrows<NoSuchElementException> { beanService.createBean(newBean) }
    }

    @Test
    fun `create bean for invalid country code`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)

        val newBean = NewBean(
            name = "Test",
            roasteryId = createdRoastery.id,
            origins = mutableSetOf("NON_EXISTING")
        )

        assertThrows<NoSuchElementException> { beanService.createBean(newBean) }
    }

    @Test
    fun `delete bean`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)

        val newBean = fakeNewBean(createdRoastery.id)

        val createdBean = beanService.createBean(newBean)
        beanService.deleteBean(UUID.fromString(createdBean.id))

        transaction {
            val origins = BeanOriginEntities.selectAll().toList()
            assertEquals(emptyList(), origins)
        }
    }

    @Test
    fun `delete non existing bean`() {
        assertThrows<NotFoundException> {
            beanService.deleteBean(UUID.randomUUID())
        }
    }

    @Test
    fun `get bean`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)
        val newBean = fakeNewBean(createdRoastery.id)
        val createdBean = beanService.createBean(newBean)

        val queriedBean = beanService.getBean(UUID.fromString(createdBean.id))

        assertEquals(createdBean, queriedBean)
    }

    @Test
    fun `get bean for invalid id`() {
        val bean = beanService.getBean(UUID.randomUUID())
        assertNull(bean)
    }

    @Test
    fun `get all beans for roastery without beans`() {
        val newRoastery = fakeNewRoastery()
        val createdRoastery = roasteryService.createRoastery(newRoastery)

        val beans = beanService.getAllBeansForRoasteryId(UUID.fromString(createdRoastery.id))

        assertEquals(emptyList(), beans)
    }

    @Test
    fun `get all beans for non existing roastery`() {
        assertThrows<NotFoundException> {
            beanService.getAllBeansForRoasteryId(UUID.randomUUID())
        }
    }

    @Test
    fun `get all beans for roastery`() {
        val newRoastery1 = fakeNewRoastery()
        val newRoastery2 = fakeNewRoastery()
        val createdRoastery1 = roasteryService.createRoastery(newRoastery1)
        val createdRoastery2 = roasteryService.createRoastery(newRoastery2)

        val newBean1 = fakeNewBean(createdRoastery1.id)
        val newBean2 = fakeNewBean(createdRoastery1.id)
        val newBean3 = fakeNewBean(createdRoastery2.id)
        val createdBean1 = beanService.createBean(newBean1)
        val createdBean2 = beanService.createBean(newBean2)
        beanService.createBean(newBean3)

        val beans = beanService.getAllBeansForRoasteryId(UUID.fromString(createdRoastery1.id))

        assertEquals(listOf(createdBean1, createdBean2), beans)
    }

    @Test
    fun `update bean`() {
        val createdRoastery1 = roasteryService.createRoastery(fakeNewRoastery())
        val createdRoastery2 = roasteryService.createRoastery(fakeNewRoastery())

        val newBean = fakeNewBean(createdRoastery1.id)
        val createdBean = beanService.createBean(newBean)

        val otherBean = fakeNewBean(createdRoastery2.id)

        val updatedBean = beanService.updateBean(otherBean, UUID.fromString(createdBean.id))

        assertEquals(otherBean.roasteryId, updatedBean.roasteryId)
        assertEquals(otherBean.origins, updatedBean.origins)
    }

    @Test
    fun `update non existing bean`() {
        val createdRoastery = roasteryService.createRoastery(fakeNewRoastery())
        val otherBean = fakeNewBean(createdRoastery.id)

        assertThrows<NoSuchElementException> {
            beanService.updateBean(otherBean, UUID.randomUUID())
        }
    }
}
