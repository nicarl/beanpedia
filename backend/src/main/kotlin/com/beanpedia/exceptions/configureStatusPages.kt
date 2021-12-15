package com.beanpedia.exceptions

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.SerializationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UUIDConversionException> {
            call.respond(
                    HttpStatusCode.NotFound
            )
        }
        exception<NotFoundException> {
            call.respond(HttpStatusCode.NotFound
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