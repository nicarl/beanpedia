package com.beanpedia.routes

import com.beanpedia.helpers.*
import com.beanpedia.installExtensions
import com.beanpedia.model.Bean

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class BeansRouteTest {
    @Test
    fun postBean() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()
            val bean = fakeNewBean(insertedRoastery.id)
            handleRequest(HttpMethod.Post, "/beans") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(bean))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val decodedResponse = Json.decodeFromString<Bean>(response.content!!)
                assertEquals(decodedResponse.name, bean.name)
            }
        }
    }

    @Test
    fun getBean() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()
            val insertedBean = insertBean(insertedRoastery.id)

            handleRequest(HttpMethod.Get, "/beans/${insertedBean.id}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(insertedBean, Json.decodeFromString(response.content!!))
            }
        }
    }

    @Test
    fun getBeanForInvalidId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Get, "/beans/invalid_id").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun getBeanForNonExistingId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Get, "/beans/${UUID.randomUUID()}").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun getAllBeans() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery1 = insertRoastery()
            val insertedBean1 = insertBean(insertedRoastery1.id)
            val insertedRoastery2 = insertRoastery()
            val insertedBean2 = insertBean(insertedRoastery2.id)

            handleRequest(HttpMethod.Get, "/beans").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(listOf(insertedBean1,
                insertedBean2), Json.decodeFromString(response.content!!))
            }
        }
    }

    @Test
    fun getAllBeansEmptyList() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Get, "/beans").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(emptyList<Bean>(), Json.decodeFromString(response.content!!))
            }
        }
    }

    @Test
    fun putBean() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()
            val insertedBean = insertBean(insertedRoastery.id)
            val updatedBean = fakeNewBean(insertedRoastery.id)

            handleRequest(HttpMethod.Put, "/beans/${insertedBean.id}") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(updatedBean))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(updatedBean.name, Json.decodeFromString<Bean>(response.content!!).name)
            }
        }
    }

    @Test
    fun putBeanForInvalidId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()
            val updatedBean = fakeNewBean(insertedRoastery.id)

            handleRequest(HttpMethod.Put, "/beans/invalid_id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(updatedBean))
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun putBeanForNonexistingId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()
            val updatedBean = fakeNewBean(insertedRoastery.id)

            handleRequest(HttpMethod.Put, "/beans/${UUID.randomUUID()}") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(updatedBean))
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun deleteBean() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()
            val insertedBean = insertBean(insertedRoastery.id)

            handleRequest(HttpMethod.Delete, "/beans/${insertedBean.id}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun deleteBeanForInvalidId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Delete, "/beans/invalid_id").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun deleteBeanForNonExistingId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Delete, "/beans/${UUID.randomUUID()}").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

}