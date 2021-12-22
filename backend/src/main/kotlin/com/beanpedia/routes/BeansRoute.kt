package com.beanpedia.routes

import com.beanpedia.model.NewBean
import com.beanpedia.service.BeanService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route

fun Route.beans(beanService: BeanService) {
    route("/beans") {
        get {
            call.respond(beanService.getAllBeans())
        }
        post {
            val bean = call.receive<NewBean>()
            val createdBean = beanService.createBean(bean)
            call.respond(createdBean)
        }

        get("/{id}") {
            val id = getUUIDFromPath()
            val bean = beanService.getBean(id)
            if (bean == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(bean)
        }

        put("/{id}") {
            val id = getUUIDFromPath()
            val bean = call.receive<NewBean>()
            val updatedBean = beanService.updateBean(bean, id)
            call.respond(updatedBean)
        }

        delete("/{id}") {
            val id = getUUIDFromPath()
            beanService.deleteBean(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}
