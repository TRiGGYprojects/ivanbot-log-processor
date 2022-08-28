package app.eelman.ivanbotlogprocessor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.data.mongodb")
class MongoProperties(val uri : String) {
}
