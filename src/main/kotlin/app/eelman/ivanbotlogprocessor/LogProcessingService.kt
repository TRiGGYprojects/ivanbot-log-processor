package app.eelman.ivanbotlogprocessor

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.springframework.stereotype.Component
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant

@Component
class LogProcessingService(coroutineDatabase: CoroutineDatabase) {

    private val eventCollection = coroutineDatabase.getCollection<PavlovEvent>("pavlov-event").apply {
        runBlocking {
            ensureIndex(
                AllStatsContainer::mapLabel,
                AllStatsContainer::gameMode,
                indexOptions = IndexOptions().sparse(true)
            )
        }
    }
    private val objectMapper = jacksonObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
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
        File("/pavlovlogs.log").inputStream().bufferedReader().use {
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
        }
        if (event !is RoundStateContainer && event !is RoundEndContainer) {
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

