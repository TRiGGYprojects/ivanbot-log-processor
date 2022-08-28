package app.eelman.ivanbotlogprocessor.config

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoConfiguration(private val mongoProperties: MongoProperties) {

    @Bean
    fun mongoClient(): CoroutineClient {
        return KMongo.createClient(mongoProperties.uri).coroutine
    }

    @Bean
    fun mongoDatabase(mongoClient: CoroutineClient): CoroutineDatabase {
        return mongoClient.getDatabase("ivanbot")
    }
}
