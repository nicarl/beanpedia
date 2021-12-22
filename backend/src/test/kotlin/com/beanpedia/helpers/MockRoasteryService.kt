package com.beanpedia.helpers

import com.beanpedia.exceptions.NotFoundException
import com.beanpedia.model.NewRoastery
import com.beanpedia.model.Roastery
import com.beanpedia.service.RoasteryService
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.test.assertEquals

class MockRoasteryService : RoasteryService {
    private var roasteries: MutableMap<UUID, Roastery> = mutableMapOf()

    private fun newRoasteryToRoastery(newRoastery: NewRoastery, uuid: UUID? = null): Roastery {
        return Roastery(
            id = uuid?.toString() ?: UUID.randomUUID().toString(),
            name = newRoastery.name,
            description = newRoastery.description,
            address = newRoastery.address,
            phoneNumber = newRoastery.phoneNumber,
            website = newRoastery.website,
            facebook = newRoastery.facebook,
            instagram = newRoastery.instagram,
            twitter = newRoastery.twitter
        )
    }

    override fun getAllRoasteries(): List<Roastery> {
        return roasteries.values.toList()
    }

    override fun getRoastery(id: UUID): Roastery? {
        return roasteries[id]
    }

    override fun createRoastery(roastery: NewRoastery): Roastery {
        val createdRoastery = newRoasteryToRoastery(roastery)
        roasteries[UUID.fromString(createdRoastery.id)] = createdRoastery
        return createdRoastery
    }

    override fun deleteRoastery(id: UUID) {
        roasteries.remove(id) ?: throw NotFoundException()
    }

    override fun updateRoastery(updatedRoastery: NewRoastery, id: UUID): Roastery {
        val createdRoastery = newRoasteryToRoastery(updatedRoastery, id)
        if (roasteries[id] == null) {
            throw NotFoundException()
        }
        roasteries[id] = createdRoastery
        return createdRoastery
    }
}

fun TestApplicationEngine.insertRoastery(): Roastery {
    val roastery = fakeNewRoastery()
    var insertedRoastery: Roastery?
    handleRequest(HttpMethod.Post, "/roasteries") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(Json.encodeToString(roastery))
    }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        insertedRoastery = Json.decodeFromString<Roastery>(response.content!!)
    }
    return insertedRoastery!!
}
