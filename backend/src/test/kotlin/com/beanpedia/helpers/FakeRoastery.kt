package com.beanpedia.helpers

import com.beanpedia.model.Address
import com.beanpedia.model.NewRoastery

fun fakeNewRoastery(countryCode: String? = null): NewRoastery = NewRoastery(
    name = faker.company.name(),
    description = faker.lorem.toString(),
    address = Address(
        address1 = faker.address.streetAddress(),
        address2 = faker.address.secondaryAddress(),
        address3 = null,
        city = faker.address.city(),
        postalCode = faker.address.postcode(),
        country = countryCode ?: faker.address.countryCode()
    ),
    phoneNumber = faker.phoneNumber.phoneNumber(),
    website = faker.internet.domain(),
    facebook = faker.internet.domain(),
    instagram = faker.internet.domain(),
    twitter = faker.internet.domain()
)
