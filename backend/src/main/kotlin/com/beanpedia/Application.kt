package com.beanpedia

import com.beanpedia.exceptions.configureStatusPages
import com.beanpedia.routes.configureRouting
import com.beanpedia.service.BeanService
import com.beanpedia.service.DatabaseBeanService
import com.beanpedia.service.DatabaseFactory
import com.beanpedia.service.DatabaseRoasteryService
import com.beanpedia.service.RoasteryService
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.serialization.json
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
