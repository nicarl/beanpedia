package com.beanpedia.routes

import com.beanpedia.exceptions.UUIDConversionException
import io.ktor.application.*
import io.ktor.util.pipeline.*
import java.util.*

fun PipelineContext<Unit, ApplicationCall>.getUUIDFromPath(): UUID {
    try {
        return UUID.fromString(call.parameters["id"])
    } catch (e: IllegalArgumentException) {
        throw UUIDConversionException()
    }
}