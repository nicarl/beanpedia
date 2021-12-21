package com.beanpedia.routes

import com.beanpedia.helpers.MockBeanService
import com.beanpedia.helpers.MockRoasteryService
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BasicEndpoints {
    @Test
    fun testNonExistingRoute() {
        withTestApplication({ configureRouting(MockBeanService(), MockRoasteryService()) }) {
            handleRequest(HttpMethod.Get, "/nonexistingroute").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }
}
