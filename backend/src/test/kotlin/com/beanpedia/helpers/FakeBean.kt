package com.beanpedia.helpers

import com.beanpedia.model.NewBean
import com.beanpedia.model.BeanComposition
import com.beanpedia.model.BeanProcessing
import com.beanpedia.model.DegreeOfRoasting
import com.beanpedia.model.NewBeanWithoutRoasteryId
import kotlin.random.Random

fun fakeNewBean(roasteryId: String): NewBean = NewBean(
    name = faker.coffee.blendName(),
    roasteryId = roasteryId,
    altitude = "${(1000..2000).random()} m",
    processing = BeanProcessing(
        isWashed = Random.nextBoolean(),
        isSemiWashed = Random.nextBoolean(),
        isNatural = Random.nextBoolean(),
    ),
    composition = BeanComposition(
        containsArabica = Random.nextBoolean(),
        containsRobusta = Random.nextBoolean(),
        arabicaFraction = Random.nextFloat(),
        robustaFraction = Random.nextFloat(),
    ),
    origins = List(Random.nextInt(0, 5)) { faker.address.countryCode() },
    degreeOfRoasting = faker.random.nextEnum<DegreeOfRoasting>(),
    description = faker.lorem.toString()
)

fun fakeNewBeanWithoutRoasteryId(): NewBeanWithoutRoasteryId = NewBeanWithoutRoasteryId(
    name = faker.coffee.blendName(),
    altitude = "${(1000..2000).random()} m",
    processing = BeanProcessing(
        isWashed = Random.nextBoolean(),
        isSemiWashed = Random.nextBoolean(),
        isNatural = Random.nextBoolean(),
    ),
    composition = BeanComposition(
        containsArabica = Random.nextBoolean(),
        containsRobusta = Random.nextBoolean(),
        arabicaFraction = Random.nextFloat(),
        robustaFraction = Random.nextFloat(),
    ),
    origins = List(Random.nextInt(0, 5)) { faker.address.countryCode() },
    degreeOfRoasting = faker.random.nextEnum<DegreeOfRoasting>(),
    description = faker.lorem.toString()
)
