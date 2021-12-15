package com.beanpedia.routes

import com.beanpedia.model.NewBeanWithoutRoasteryId
import com.beanpedia.model.NewRoastery
import com.beanpedia.service.BeanService
import com.beanpedia.service.RoasteryService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.roasteries(roasteryService: RoasteryService, beanService: BeanService) {
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
                val id = getUUIDFromPath()
                val roastery = roasteryService.getRoastery(id)
                if (roastery == null) call.respond(HttpStatusCode.NotFound)
                else call.respond(roastery)
            }

            put {
                val id = getUUIDFromPath()
                val roastery = call.receive<NewRoastery>()
                val updatedRoastery = roasteryService.updateRoastery(roastery, id)
                call.respond(updatedRoastery)
            }

            delete {
                val id = getUUIDFromPath()
                roasteryService.deleteRoastery(id)
                call.respond(HttpStatusCode.OK)
            }

            route("beans"){
                get {
                    val id = getUUIDFromPath()
                    val roastery = roasteryService.getRoastery(id)
                    if (roastery == null) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        val beans = beanService.getAllBeansForRoasteryId(id)
                        call.respond(beans)
                    }
                }

                post {
                    val id = getUUIDFromPath()
                    val bean = call.receive<NewBeanWithoutRoasteryId>()
                    val roastery = roasteryService.getRoastery(id)
                    if (roastery == null) {call.respond(HttpStatusCode.NotFound)} else {
                        val insertedBean = beanService.createBean(
                                bean.toNewBean(roastery.id)
                        )
                        call.respond(insertedBean)
                    }
                }
            }
        }
    }
}
