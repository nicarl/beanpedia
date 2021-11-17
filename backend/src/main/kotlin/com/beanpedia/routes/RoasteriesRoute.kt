package com.beanpedia.routes

import com.beanpedia.model.NewRoastery
import com.beanpedia.service.RoasteryService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun Route.roasteries(roasteryService: RoasteryService) {
    route("/roasteries") {
        get {
            call.respond(roasteryService.getAllRoasteries())
        }

        post {
            val roastery = call.receive<NewRoastery>()
            val insertedRoastery = roasteryService.createRoastery(roastery)
            call.respond(insertedRoastery)
        }

        route("/{id}") {
            get {
                val id = UUID.fromString(call.parameters["id"]) ?: throw IllegalStateException("Must provide id")
                val roastery = roasteryService.getRoastery(id)
                if (roastery == null) call.respond(HttpStatusCode.NotFound)
                else call.respond(roastery)
            }

            put {
                val id = UUID.fromString(call.parameters["id"]) ?: throw IllegalStateException("Must provide id")
                val roastery = call.receive<NewRoastery>()
                val updatedRoastery = roasteryService.updateRoastery(roastery, id)
                call.respond(roastery)
            }

            delete {
                val id = UUID.fromString(call.parameters["id"]) ?: throw IllegalStateException("Must provide id")
                roasteryService.deleteRoastery(id)
                call.respond(HttpStatusCode.OK)
            }

            route("/beans") {
                get {
                    call.respond(HttpStatusCode.OK)
                }

                post {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
