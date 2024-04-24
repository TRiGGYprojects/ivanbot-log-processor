package app.eelman.ivanbotlogprocessor

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    JsonSubTypes.Type(AllStatsEvent::class, name = "allstats"),
    JsonSubTypes.Type(RoundEndEvent::class, name = "roundend"),
    JsonSubTypes.Type(BomDataEvent::class, name = "bombdata"),
    JsonSubTypes.Type(RoundStateEvent::class, name = "roundstate"),
    JsonSubTypes.Type(KillDataEvent::class, name = "killdata"),
    JsonSubTypes.Type(SwitchTeamEvent::class, name = "switchteam"),
)
sealed interface PavlovEvent {
    var _id: EventId?
}

data class EventId(val date: Instant, val counter: Int)


data class AllStatsEvent(
    val allStats: List<AllStats>,
    val mapLabel: String,
    var gameMode: String,
    val playerCount: Int,
    val team0Score: Int?,
    val team1Score: Int?,
    val bTeams: Boolean?,
    override var _id: EventId?,
) : PavlovEvent

data class Stats(

    val statType: String,
    val amount: Int
)

data class AllStats(

    val uniqueId: String,
    val playerName: String,
    val productId: String,
    val teamId: Int,
    val stats: List<Stats>
)

data class RoundEnd(
    val round: Int,
    val winningTeam: Int
)

data class RoundEndEvent(

    val roundEnd: RoundEnd,
    override var _id: EventId?,
) : PavlovEvent

data class BomDataEvent(
    val bombData: BombData,
    override var _id: EventId?,
) : PavlovEvent

data class BombData(
    val player: String?,
    val bombInteraction: String
)

data class RoundStateEvent(
    val roundState: RoundState,
    override var _id: EventId?,
) : PavlovEvent

data class RoundState(
    val state: String,
    val timestamp: String
)

data class KillDataEvent(
    val killData: KillData,
    override var _id: EventId?,
) : PavlovEvent

data class KillData(
    val killer: String,
    val killerTeamID: Int?,
    val killed: String,
    val killedTeamID: Int?,
    val killedBy: String,
    val headshot: Boolean
)

data class SwitchTeamEvent(

    val switchTeam: SwitchTeam,
    override var _id: EventId?,
) : PavlovEvent

data class SwitchTeam(

    val playerID: String,
    val newTeamID: Int
)
