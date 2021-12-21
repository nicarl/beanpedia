package com.beanpedia.helpers

import io.github.serpro69.kfaker.faker
import java.util.Random

val faker = faker { fakerConfig { random = Random(42) } }
