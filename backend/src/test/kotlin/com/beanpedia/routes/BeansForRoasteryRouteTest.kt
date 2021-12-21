package com.beanpedia.routes

import com.beanpedia.helpers.MockBeanService
import com.beanpedia.helpers.MockRoasteryService
import com.beanpedia.helpers.insertRoastery
import com.beanpedia.helpers.fakeNewBeanWithoutRoasteryId
import com.beanpedia.helpers.insertBean
import com.beanpedia.installExtensions
import com.beanpedia.model.Bean
import io.ktor.http.HttpMethod
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.withTestApplication
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class BeansForRoasteryRouteTest {
    @Test
    fun postBean() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery1 = insertRoastery()
            insertRoastery()

            val newBeanWithoutRoasteryId = fakeNewBeanWithoutRoasteryId()

            handleRequest(HttpMethod.Post, "roasteries/${insertedRoastery1.id}/beans") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(newBeanWithoutRoasteryId))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(newBeanWithoutRoasteryId.name, Json.decodeFromString<Bean>(response.content!!).name)
            }
        }
    }

    @Test
    fun postBeanForNonexistingRoastery() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val newBeanWithoutRoasteryId = fakeNewBeanWithoutRoasteryId()
            handleRequest(HttpMethod.Post, "roasteries/${UUID.randomUUID()}/beans") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(newBeanWithoutRoasteryId))
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun getBeans() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery1 = insertRoastery()
            val insertedRoastery2 = insertRoastery()

            val insertedBean1 = insertBean(insertedRoastery1.id)
            val insertedBean2 = insertBean(insertedRoastery1.id)
            insertBean(insertedRoastery2.id)

            handleRequest(HttpMethod.Get, "roasteries/${insertedRoastery1.id}/beans").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(listOf(insertedBean1, insertedBean2), Json.decodeFromString(response.content!!))
            }
        }
    }

    @Test
    fun getBeansForNonexistingRoastery() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery1 = insertRoastery()
            val insertedRoastery2 = insertRoastery()

            insertBean(insertedRoastery1.id)
            insertBean(insertedRoastery1.id)
            insertBean(insertedRoastery2.id)

            handleRequest(HttpMethod.Get, "roasteries/${UUID.randomUUID()}/beans").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }
}
