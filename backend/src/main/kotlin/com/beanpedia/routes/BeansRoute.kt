package com.beanpedia.routes

import com.beanpedia.model.NewBean
import com.beanpedia.service.BeanService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

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
            val id = UUID.fromString(call.parameters["id"]) ?: throw IllegalStateException("Must provide id")
            val bean = beanService.getBean(id)
            if (bean == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(bean)
        }

        put("/{id}") {}

        delete("/{id}") {
        }
    }
}
