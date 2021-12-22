package com.beanpedia.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.status() {
    route("/status") {
        get {
            call.response.status(HttpStatusCode.OK)
        }
    }
}
