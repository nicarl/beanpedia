package com.beanpedia.helpers

import com.beanpedia.exceptions.NotFoundException
import com.beanpedia.model.Bean
import com.beanpedia.model.NewBean
import com.beanpedia.service.BeanService
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.test.assertEquals

class MockBeanService: BeanService {
    private var beans: MutableMap<UUID, Bean> = mutableMapOf()

    private fun newBeanToBean(newBean: NewBean, uuid: UUID? = null): Bean {
        return Bean(
                id = uuid?.toString() ?: UUID.randomUUID().toString(),
                name = newBean.name,
                roasteryId = newBean.roasteryId,
                altitude = newBean.altitude,
                processing = newBean.processing,
                composition = newBean.composition,
                origins = newBean.origins,
                degreeOfRoasting = newBean.degreeOfRoasting,
                description = newBean.description
        )
    }

    override fun createBean(bean: NewBean): Bean {
        val uuid = UUID.randomUUID()
        val createdBean = Bean(
                id=uuid.toString(),
                name=bean.name,
                roasteryId = bean.roasteryId,
                altitude = bean.altitude,
                processing = bean.processing,
                composition = bean.composition,
                origins = bean.origins,
                degreeOfRoasting = bean.degreeOfRoasting,
                description = bean.description
        )
        beans[uuid] = createdBean
        return createdBean
    }

    override fun getAllBeans(): List<Bean> {
        return beans.values.toList()
    }

    override fun getBean(id: UUID): Bean? {
        return beans[id]
    }

    override fun updateBean(updatedBean: NewBean, id: UUID): Bean {
        val createdBean = newBeanToBean(updatedBean, id)
        if (beans[id] == null) {
            throw NotFoundException()
        }
        beans[id] = createdBean
        return createdBean
    }

    override fun deleteBean(id: UUID) {
        beans.remove(id) ?: throw NotFoundException()
    }

    override fun getAllBeansForRoasteryId(roasteryId: UUID): List<Bean> {
        return beans.values.toList().filter { it.roasteryId == roasteryId.toString() }
    }
}



fun TestApplicationEngine.insertBean(roasteryId: String): Bean {
    val bean = fakeNewBean(roasteryId)
    var insertedBean: Bean?
    handleRequest(HttpMethod.Post, "/beans") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(bean))
    }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        insertedBean = Json.decodeFromString<Bean>(response.content!!)
    }
    return insertedBean!!
}
