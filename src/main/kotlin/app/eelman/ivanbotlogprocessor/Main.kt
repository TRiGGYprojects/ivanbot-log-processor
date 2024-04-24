package app.eelman.ivanbotlogprocessor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
@ConfigurationPropertiesScan
@SpringBootApplication
class IvanBotLogProcessor

fun main(args: Array<String>) {
    runApplication<IvanBotLogProcessor>(*args)
}

