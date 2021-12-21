package com.beanpedia.routes

import com.beanpedia.service.BeanService
import com.beanpedia.service.RoasteryService
import io.ktor.application.Application
import io.ktor.routing.routing

fun Application.configureRouting(
    beanService: BeanService,
    roasteryService: RoasteryService
) {
    routing {
        status()
        beans(beanService)
        roasteries(roasteryService, beanService)
    }
}
