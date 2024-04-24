package app.eelman.ivanbotlogprocessor.service

import app.eelman.ivanbotlogprocessor.AllStatsEvent
import app.eelman.ivanbotlogprocessor.EventId
import app.eelman.ivanbotlogprocessor.ParsingContext
import app.eelman.ivanbotlogprocessor.PavlovEvent
import app.eelman.ivanbotlogprocessor.RoundEndEvent
import app.eelman.ivanbotlogprocessor.RoundStateEvent
import app.eelman.ivanbotlogprocessor.config.PavlovConfiguration
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.springframework.stereotype.Component
import java.io.File
import java.time.Instant

@Component
class LogProcessingService(coroutineDatabase: CoroutineDatabase,private val pavlovConfiguration: PavlovConfiguration) {

    private val eventCollection = coroutineDatabase.getCollection<PavlovEvent>("pavlov-event").apply {
        runBlocking {
            ensureIndex(
                AllStatsEvent::mapLabel,
                AllStatsEvent::gameMode,
                indexOptions = IndexOptions().sparse(true)
            )
        }
    }
    private val objectMapper = jacksonMapperBuilder()
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build()
    final val job: Job
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        job = coroutineScope.launch {
            handleProcessingOfLogs()
        }
    }


    suspend fun handleProcessingOfLogs() {
        val last = eventCollection.find().descendingSort(PavlovEvent::_id / EventId::date).limit(1).first()
        val parseContext = ParsingContext(last)
        File(pavlovConfiguration.logPath).inputStream().bufferedReader().use {
            while (true) {
                val line = it.readLine()
                if (line == null) {
                    Thread.sleep(1000)
                } else {
                    parseContext.handleNewLine(line)?.let { (buffer, date, counter) ->
                        handleEvent(buffer, date, counter)
                    }
                }
            }
        }

    }


    private fun handleEvent(buffer: StringBuffer, eventDate: Instant, counter: Int) {
        val event = objectMapper.readValue<PavlovEvent>(buffer.toString()).apply {
            this._id = EventId(eventDate, counter)
        }.apply {
            (this as? AllStatsEvent)?.let { it.gameMode = it.gameMode.trim() }
        }
        if (event !is RoundStateEvent && event !is RoundEndEvent) {
            runBlocking {
                eventCollection.replaceOne(
                    PavlovEvent::_id eq EventId(eventDate, counter),
                    event,
                    options = ReplaceOptions().upsert(true)
                )
            }
        }

    }


}

