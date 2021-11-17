package com.beanpedia

import com.beanpedia.routes.configureRouting
import com.beanpedia.service.BeanService
import com.beanpedia.service.DatabaseFactory
import com.beanpedia.service.RoasteryService
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0",) {
        install(DefaultHeaders)
        install(CallLogging)
        install(ContentNegotiation) {
            json()
        }

        DatabaseFactory.connectAndMigrate()
        val beanService = BeanService()
        val roasteryService = RoasteryService()

        configureRouting(beanService, roasteryService)
    }.start(wait = true)
}
