package com.beanpedia

import com.beanpedia.helpers.MockBeanService
import com.beanpedia.helpers.MockRoasteryService
import com.beanpedia.routes.configureRouting
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StatusTest {
    @Test
    fun testStatus() {
        withTestApplication({ configureRouting(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Get, "/status").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(null, response.content)
            }
        }
    }

    @Test
    fun testNonExistingRoute() {
        withTestApplication({ configureRouting(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Get, "/nonexistingroute").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }
}
