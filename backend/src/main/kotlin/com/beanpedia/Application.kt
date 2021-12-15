package com.beanpedia

import com.beanpedia.exceptions.configureStatusPages
import com.beanpedia.routes.configureRouting
import com.beanpedia.service.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun Application.installExtensions(beanService: BeanService, roasteryService: RoasteryService) {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    configureStatusPages()
    configureRouting(beanService, roasteryService)
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0",) {
        DatabaseFactory.connectAndMigrate()
        val beanService = DatabaseBeanService()
        val roasteryService = DatabaseRoasteryService()

        installExtensions(beanService, roasteryService)
    }.start(wait = true)
}
