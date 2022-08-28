package app.eelman.ivanbotlogprocessor

import java.text.SimpleDateFormat
import java.time.Instant

class ParsingContext(private val event: PavlovEvent?) {
    private val dateRegex = Regex("^\\[([\\d.\\-:]+)]\\[\\d+]StatManagerLog:(.*)")

    private val dateformat = SimpleDateFormat("yyyy.MM.dd-HH.mm.ss:SSS")
    private var counter = 0
    private var lastTimeStamp: Instant? = null
    private var buffer = StringBuffer()
    private var accolateCounter = 0
    private var isReadingJson = false
    private var eventDate: Instant? = null


    fun handleNewLine(line: String): Triple<StringBuffer, Instant, Int>? {
        val match = dateRegex.matchEntire(line)
        when {
            match != null -> {
                eventDate = dateformat.parse(match.groups[1]?.value).toInstant()
                if (event == null || eventDate!! > event._id?.date) {
                    isReadingJson = true
                    val lineEnding = match.groups[2]?.value ?: throw InternalError("Match group empty")
                    buffer = StringBuffer(lineEnding)
                    accolateCounter = calculateAccolateNetAmount(lineEnding)

                }
            }
            isReadingJson && accolateCounter != 0 -> {
                buffer.append(line.sanitizePavlovThings())
                accolateCounter += calculateAccolateNetAmount(line)
                if (accolateCounter == 0) {
                    if (eventDate == lastTimeStamp) {
                        counter++
                    } else {
                        lastTimeStamp = eventDate
                        counter = 0
                    }
                    return Triple(buffer, eventDate!!, counter)
                }

            }
        }
        return null
    }

    private fun calculateAccolateNetAmount(line: String): Int {
        val added = line.count { it == '{' }
        val subtracted = line.count { it == '}' } * -1
        return added + subtracted
    }

    private fun String.sanitizePavlovThings(): String {
        return this.replace("LogTemp: Error: Lazy Loaded obj but we already had a ref, this is an odd case", "")
    }
}

