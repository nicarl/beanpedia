package com.beanpedia.exceptions

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.serialization.SerializationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UUIDConversionException> {
            call.respond(
                HttpStatusCode.NotFound
            )
        }
        exception<NotFoundException> {
            call.respond(
                HttpStatusCode.NotFound
            )
        }
        exception<SerializationException> {
            call.respond(HttpStatusCode.UnprocessableEntity)
        }
        exception<NoSuchElementException> {
            call.respond(
                HttpStatusCode.BadRequest
            )
        }
    }
}
