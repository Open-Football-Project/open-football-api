package org.footballproject.model

import org.footballproject.clientData.SquadPlayer
import java.io.Serializable

enum class ScoringSignal { ODDS_IMPLIED, SEASON_STAT }

enum class PlayerPosition(val position: String) {
    GOALKEEPER("Goalkeeper"),
    DEFENDER("Defender"),
    MIDFIELDER("Midfielder"),
    ATTACKER("Attacker");

    companion object {
        fun fromApiPosition(raw: String): PlayerPosition? =
            entries.find { it.position.equals(raw.trim(), ignoreCase = true) }
    }
}

data class PlayerScore(
    val player: SquadPlayer,
    val score: Double,
    val signal: ScoringSignal,
    val reason: ScoreReason
) : Serializable


data class TodayPlayersWatchlist(
    val fixtureId: Int,
    val leagueId: Int,
    val leagueName: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val home: Map<PlayerPosition, List<PlayerScore>>,
    val away: Map<PlayerPosition, List<PlayerScore>>
) : Serializable


sealed class ScoreReason : Serializable {
    data class MarketOdds(val markets: List<String>) : ScoreReason()

    data class SeasonForm(
        val appearances: Int,
        val goals: Int,
        val assists: Int,
        val rating: Double
    ) : ScoreReason()
}
