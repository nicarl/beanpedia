package com.beanpedia.routes

import com.beanpedia.helpers.MockBeanService
import com.beanpedia.helpers.MockRoasteryService
import com.beanpedia.helpers.fakeNewRoastery
import com.beanpedia.helpers.insertRoastery
import com.beanpedia.installExtensions
import com.beanpedia.model.Roastery
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.ContentType
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

class RoasteriesRouteTest {
    @Test
    fun postRoastery() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val roastery = fakeNewRoastery()
            handleRequest(HttpMethod.Post, "/roasteries") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(roastery))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val decodedResponse = Json.decodeFromString<Roastery>(response.content!!)
                assertEquals(decodedResponse.name, roastery.name)
            }
        }
    }

    @Test
    fun postRoasteryWithInvalidInput() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Post, "/roasteries") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("some invalid input")
            }.apply {
                assertEquals(HttpStatusCode.UnprocessableEntity, response.status())
            }
        }
    }

    @Test
    fun putRoasteryForInvalidId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val roastery = fakeNewRoastery()

            handleRequest(HttpMethod.Put, "/roasteries/invalid_id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(roastery))
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun putRoasteryForNonExistingId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val roastery = fakeNewRoastery()
            val uuid = UUID.randomUUID().toString()

            handleRequest(HttpMethod.Put, "/roasteries/$uuid") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(roastery))
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun putRoastery() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()
            val updatedRoastery = fakeNewRoastery()

            handleRequest(HttpMethod.Put, "/roasteries/${insertedRoastery.id}") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(updatedRoastery))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(updatedRoastery.name, Json.decodeFromString<Roastery>(response.content!!).name)
            }
        }
    }

    @Test
    fun deleteRoasteryForNonExistingId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val uuid = UUID.randomUUID().toString()

            handleRequest(HttpMethod.Delete, "/roasteries/$uuid") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun deleteRoasteryForInvalidId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Delete, "/roasteries/invalid_id") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun deleteRoastery() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()

            handleRequest(HttpMethod.Delete, "/roasteries/${insertedRoastery.id}") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun getRoastery() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val insertedRoastery = insertRoastery()

            handleRequest(HttpMethod.Get, "/roasteries/${insertedRoastery.id}").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(insertedRoastery, Json.decodeFromString(response.content!!))
            }
        }
    }

    @Test
    fun getRoasteryForNonExistingId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val nonExistingId = UUID.randomUUID()
            handleRequest(HttpMethod.Get, "/roasteries/$nonExistingId").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun getRoasteryForInvalidId() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val noUUID = "noUUID"
            handleRequest(HttpMethod.Get, "/roasteries/$noUUID").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun getAllRoasteriesEmptyList() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Get, "/roasteries").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(emptyList<Roastery>(), Json.decodeFromString(response.content!!))
            }
        }
    }

    @Test
    fun getAllRoasteries() {
        withTestApplication({ installExtensions(MockBeanService(), MockRoasteryService()) }) {
            val roastery1 = insertRoastery()
            val roastery2 = insertRoastery()

            handleRequest(HttpMethod.Get, "/roasteries").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(listOf(roastery1, roastery2), Json.decodeFromString(response.content!!))
            }
        }
    }
}
