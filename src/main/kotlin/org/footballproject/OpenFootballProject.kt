package org.footballproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
@EnableScheduling
class OpenFootballProject

fun main(args: Array<String>) {
    runApplication<OpenFootballProject>(*args)
}
