package match.insights.clientData

import java.io.Serializable


data class LiveFixtureResponse(
    val fixture: Fixture,
    val league: League,
    val teams: Teams,
    val goals: Goal,
    val score: Score,
    val events: List<Event>
) : Serializable



