package com.beanpedia.routes

import com.beanpedia.exceptions.UUIDConversionException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.util.pipeline.PipelineContext
import java.util.UUID

fun PipelineContext<Unit, ApplicationCall>.getUUIDFromPath(): UUID {
    try {
        return UUID.fromString(call.parameters["id"])
    } catch (e: IllegalArgumentException) {
        throw UUIDConversionException(e)
    }
}
