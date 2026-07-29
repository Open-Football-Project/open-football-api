package match.insights.model

import java.io.Serializable
import java.time.Instant

data class LiveOddsSnapshot(
    val label: String,
    val odd: String,
    val minute: Int,
    val capturedAt: Instant
) : Serializable

data class LiveOddsMarketEntry(
    val marketId: Int,
    val marketName: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val history: List<LiveOddsSnapshot>
) : Serializable

data class LiveOddsFixture(
    val fixtureId: Int,
    val homeTeamName: String,
    val awayTeamName: String
) : Serializable
