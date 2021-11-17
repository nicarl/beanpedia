package com.beanpedia.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*

fun Route.status() {
    route("/status") {
        get {
            call.response.status(HttpStatusCode.OK)
        }
    }
}
