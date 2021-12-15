package com.beanpedia.service

import com.beanpedia.helpers.DatabaseTest
import com.beanpedia.helpers.fakeNewBean
import com.beanpedia.helpers.fakeNewRoastery
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class DatabaseBeanServiceTest: DatabaseTest() {
    private val roasteryService = DatabaseRoasteryService()
    private val beanService = DatabaseBeanService()

    @Test
    fun `get all beans for empty database`() {
        assertEquals(emptyList(), beanService.getAllBeans())
    }

    @Test
    fun `get all beans`() {
        val newRoastery1= fakeNewRoastery()
        val newRoastery2 = fakeNewRoastery()
        val createdRostery1 = roasteryService.createRoastery(newRoastery1)
        val createdRostery2 = roasteryService.createRoastery(newRoastery2)
        val newBean1 = fakeNewBean(createdRostery1.id)
        val newBean2 = fakeNewBean(createdRostery2.id)

        val createdBean1 = beanService.createBean(newBean1)
        val createdBean2 = beanService.createBean(newBean2)

        assertEquals(listOf(createdBean1, createdBean2), beanService.getAllBeans())
    }

}