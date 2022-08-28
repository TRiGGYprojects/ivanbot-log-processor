package app.eelman.ivanbotlogprocessor

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    JsonSubTypes.Type(AllStatsContainer::class, name = "allstats"),
    JsonSubTypes.Type(RoundEndContainer::class, name = "roundend"),
    JsonSubTypes.Type(BomDataContainer::class, name = "bombdata"),
    JsonSubTypes.Type(RoundStateContainer::class, name = "roundstate"),
    JsonSubTypes.Type(KillDataContainer::class, name = "killdata"),
    JsonSubTypes.Type(SwitchTeamContainer::class, name = "switchteam"),
)
sealed interface PavlovEvent {
    var _id: EventId?
}

data class EventId(val date: Instant, val counter: Int)


data class AllStatsContainer(

    val allStats: List<AllStats>,
    val mapLabel: String,
    val gameMode: String,
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
    val teamId: Int,
    val stats: List<Stats>
)

data class RoundEnd(
    val round: Int,
    val winningTeam: Int
)

data class RoundEndContainer(

    val roundEnd: RoundEnd,
    override var _id: EventId?,
) : PavlovEvent

data class BomDataContainer(
    val bombData: BombData,
    override var _id: EventId?,
) : PavlovEvent

data class BombData(
    val player: String?,
    val bombInteraction: String
)

data class RoundStateContainer(
    val roundState: RoundState,
    override var _id: EventId?,
) : PavlovEvent

data class RoundState(
    val state: String,
    val timestamp: String
)

data class KillDataContainer(
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

data class SwitchTeamContainer(

    val switchTeam: SwitchTeam,
    override var _id: EventId?,
) : PavlovEvent

data class SwitchTeam(

    val playerID: String,
    val newTeamID: Int
)
