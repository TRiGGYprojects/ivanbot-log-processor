package app.eelman.ivanbotlogprocessor.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.data.mongodb")
class MongoProperties(val uri : String) {
}
