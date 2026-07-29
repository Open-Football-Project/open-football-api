package match.insights.clientData

import java.io.Serializable

data class TeamLeagueParticipation(
    val league: TeamLeagueParticipationInfo,
    val country: TeamLeagueParticipationCountry,
    val seasons: List<TeamLeagueParticipationSeasonInfo>
) : Serializable

data class TeamLeagueParticipationInfo(
    val id: Int,
    val name: String,
    val type: String,
    val logo: String?
) : Serializable

data class TeamLeagueParticipationCountry(
    val name: String,
    val code: String?,
    val flag: String?
) : Serializable

data class TeamLeagueParticipationSeasonInfo(
    val year: Int,
    val start: String,
    val end: String,
    val current: Boolean,
    val coverage: TeamLeagueParticipationCoverage
) : Serializable

data class TeamLeagueParticipationCoverage(
    val fixtures: TeamLeagueParticipationFixtureCoverage,
    val standings: Boolean,
    val players: Boolean,
    val top_scorers: Boolean,
    val top_assists: Boolean,
    val top_cards: Boolean,
    val injuries: Boolean,
    val predictions: Boolean,
    val odds: Boolean
) : Serializable

data class TeamLeagueParticipationFixtureCoverage(
    val events: Boolean,
    val lineups: Boolean,
    val statistics_fixtures: Boolean,
    val statistics_players: Boolean
) : Serializable
