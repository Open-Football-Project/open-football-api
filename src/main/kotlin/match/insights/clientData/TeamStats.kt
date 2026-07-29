package match.insights.clientData;

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


data class ClientTeamStatistics(
    val league: TeamStatsLeagueInfo,
    val team: TeamStatsInfo,
    val form: String?,
    val fixtures: TeamStatsFixtures,
    val goals: TeamStatsGoals,
    val biggest: TeamStatsBiggest,
    val clean_sheet: TeamStatsTotals,
    val failed_to_score: TeamStatsTotals,
    val penalty: TeamStatsPenalty,
    val lineups: List<TeamStatsLineup>,
    val cards: TeamStatsCards
) : Serializable

data class TeamStatsLeagueInfo(
    val id: Int?,
    val name: String?,
    val country: String?,
    val logo: String?,
    val flag: String?,
    val season: Int?
) : Serializable

data class TeamStatsInfo(
    val id: Int,
    val name: String,
    val logo: String?
) : Serializable

data class TeamStatsFixtures(
    val played: TeamStatsTotals,
    val wins: TeamStatsTotals,
    val draws: TeamStatsTotals,
    val loses: TeamStatsTotals
) : Serializable

data class TeamStatsTotals(
    val home: Int?,
    val away: Int?,
    val total: Int?
) : Serializable

data class TeamStatsGoals(
    @JsonProperty("for")
    val forward: TeamStatsGoalsForAgainst,
    val against: TeamStatsGoalsForAgainst
) : Serializable

data class TeamStatsGoalsForAgainst(
    val total: TeamStatsTotals,
    val average: TeamStatsAvg,
    val minute: Map<String, TeamStatsGoalPeriod?>,
    val under_over: Map<String, TeamStatsOverUnder>
) : Serializable

data class TeamStatsAvg(
    val home: String?,
    val away: String?,
    val total: String?
) : Serializable

data class TeamStatsGoalPeriod(
    val total: Int?,
    val percentage: String?
) : Serializable

data class TeamStatsOverUnder(
    val over: Int?,
    val under: Int?
) : Serializable

data class TeamStatsBiggest(
    val streak: TeamStatsStreak,
    val wins: TeamStatsHomeAwayScore,
    val loses: TeamStatsHomeAwayScore,
    val goals: TeamStatsGoalsHomeAway
) : Serializable

data class TeamStatsStreak(
    val wins: Int?,
    val draws: Int?,
    val loses: Int?
) : Serializable

data class TeamStatsHomeAwayScore(
    val home: String?,
    val away: String?
) : Serializable

data class TeamStatsGoalsHomeAway(
    @JsonProperty("for")
    val forward: TeamStatsHomeAwayGoals,
    val against: TeamStatsHomeAwayGoals
) : Serializable

data class TeamStatsHomeAwayGoals(
    val home: Int?,
    val away: Int?
) : Serializable

data class TeamStatsPenalty(
    val scored: TeamStatsPenaltyStats,
    val missed: TeamStatsPenaltyStats,
    val total: Int?
) : Serializable

data class TeamStatsPenaltyStats(
    val total: Int?,
    val percentage: String?
) : Serializable

data class TeamStatsLineup(
    val formation: String,
    val played: Int
) : Serializable

data class TeamStatsCards(
    val yellow: Map<String, TeamStatsCardInfo>,
    val red: Map<String, TeamStatsCardInfo>
) : Serializable

data class TeamStatsCardInfo(
    val total: Int?,
    val percentage: String?
) : Serializable
