package match.insights.datamanipulation

import match.insights.clientData.ClientTeamStatistics
import match.insights.clientData.LiveStatistic
import match.insights.clientData.LiveStatisticType
import match.insights.clientData.LiveTeamStats
import match.insights.clientData.TeamStatsFixtures
import match.insights.clientData.TeamStatsGoals
import match.insights.clientData.TeamStatsPenalty
import match.insights.clientData.TeamStatsTotals
import match.insights.clientData.statisticType
import match.insights.response.TeamStatistic
import match.insights.response.TeamStats
import match.insights.response.TwoTeamsStatistics
import org.springframework.stereotype.Component

@Component
class TeamStatisticsManipulation {

    fun twoTeamsStats(teams: List<LiveTeamStats>): TwoTeamsStatistics =
        TwoTeamsStatistics(
            if (teams.isEmpty()) TeamStats() else teamStats(teams[0]),
            if (teams.isNotEmpty() && teams.size > 1) teamStats(teams[1]) else TeamStats()
        )

    fun twoTeamsStats(team0: ClientTeamStatistics?, team1: ClientTeamStatistics?): TwoTeamsStatistics =
        TwoTeamsStatistics(
            team0?.let { teamStats(team0) } ?: TeamStats(),
            team1?.let { teamStats(team1) } ?: TeamStats()
        )

    fun teamStats(teamStats: ClientTeamStatistics): TeamStats =
        TeamStats(
            teamStats.team.id,
            teamStats.team.logo ?: "",
            teamStats.team.name,
            statistics = teamStatistics(teamStats)
        )

    fun teamStats(teamStats: LiveTeamStats): TeamStats =
        TeamStats(
            teamStats.team.id,
            teamStats.team.logo,
            teamStats.team.name,
            statistics = teamStatistics(teamStats.statistics)
        )

    fun teamStatistics(stats: List<LiveStatistic>): List<TeamStatistic> {
        val totalShots = requiredStatValue(stats, LiveStatisticType.TOTAL_SHOTS)
        val totalShotsOut = requiredStatValue(stats, LiveStatisticType.SHOTS_OUTSIDEBOX)
        val totalShotsIn = requiredStatValue(stats, LiveStatisticType.SHOTS_INSIDEBOX)
        val totalPasses = requiredStatValue(stats, LiveStatisticType.TOTAL_PASSES)

        return stats.filterNot {
            it.statisticType in setOf(LiveStatisticType.TOTAL_SHOTS, LiveStatisticType.TOTAL_PASSES)
        }.map {
            TeamStatistic(
                it.type, liveStatNumericValue(it.value),
                liveTotals(
                    it.statisticType,
                    totalInBox = totalShotsIn,
                    totalOutBox = totalShotsOut,
                    totalShots = totalShots,
                    totalPasses = totalPasses
                ), isLivePositive(it.statisticType)
            )
        }

    }

    private fun requiredStatValue(stats: List<LiveStatistic>, type: LiveStatisticType): Int =
        liveStatNumericValue(stats.first { it.statisticType == type }.value ?: 0)


    fun teamStatistics(clientData: ClientTeamStatistics): List<TeamStatistic> {
        return seasonFixturesStats(clientData.fixtures) +
                seasonGoalsStats(clientData.goals) +
                seasonStatsTotals(true, "Clean Sheets", clientData.clean_sheet) +
                seasonStatsTotals(false, "Failed to Score", clientData.failed_to_score) +
                seasonPenaltyStats(clientData.penalty)
    }

    fun seasonStatsTotals(
        isPositive: Boolean = false,
        prefix: String,
        statsTotal: TeamStatsTotals
    ): List<TeamStatistic> {
        return listOf(
            TeamStatistic(
                name = "$prefix at Home",
                value = statsTotal.home ?: 0,
                total = statsTotal.total ?: 0,
                isPositive = isPositive
            ),
            TeamStatistic(
                name = "$prefix Away",
                value = statsTotal.away ?: 0,
                total = statsTotal.total ?: 0,
                isPositive = isPositive
            )
        )
    }


    fun seasonPenaltyStats(penaltiesStats: TeamStatsPenalty): List<TeamStatistic> {
        return listOf(
            TeamStatistic(
                name = "Penalties Scored",
                value = penaltiesStats.scored.total ?: 0,
                total = penaltiesStats.total ?: 0,
                isPositive = true
            ),
            TeamStatistic(
                name = "Penalties Missed",
                value = penaltiesStats.missed.total ?: 0,
                total = penaltiesStats.total ?: 0,
                isPositive = false
            )
        )
    }


    fun seasonGoalsStats(goals: TeamStatsGoals): List<TeamStatistic> {
        return listOf(
            TeamStatistic(
                name = "Goals Forward at Home",
                value = goals.forward.total.home ?: 0,
                total = goals.forward.total.total ?: 0,
                isPositive = true
            ),
            TeamStatistic(
                name = "Goals Forward Away",
                value = goals.forward.total.away ?: 0,
                total = goals.forward.total.total ?: 0,
                isPositive = true
            ),
            TeamStatistic(
                name = "Goals Against at Home",
                value = goals.against.total.home ?: 0,
                total = goals.against.total.total ?: 0,
                isPositive = false
            ),

            TeamStatistic(
                name = "Goals Against Away",
                value = goals.against.total.away ?: 0,
                total = goals.against.total.total ?: 0,
                isPositive = false
            )
        )
    }

    fun seasonFixturesStats(fixtures: TeamStatsFixtures): List<TeamStatistic> {
        return listOf(
            TeamStatistic(
                name = "Won at Home",
                value = fixtures.wins.home ?: 0,
                total = fixtures.wins.total ?: 0,
                isPositive = true
            ),
            TeamStatistic(
                name = "Won Away",
                value = fixtures.wins.away ?: 0,
                total = fixtures.wins.total ?: 0,
                isPositive = true
            ),
            TeamStatistic(
                name = "Lost at Home",
                value = fixtures.loses.home ?: 0,
                total = fixtures.loses.total ?: 0,
                isPositive = false
            ),
            TeamStatistic(
                name = "Lost Away",
                value = fixtures.loses.away ?: 0,
                total = fixtures.loses.total ?: 0,
                isPositive = false
            ),

            TeamStatistic(
                name = "Draws at Home",
                value = fixtures.draws.home ?: 0,
                total = fixtures.draws.total ?: 0,
                isPositive = true
            ),
            TeamStatistic(
                name = "Draws Away",
                value = fixtures.draws.away ?: 0,
                total = fixtures.draws.total ?: 0,
                isPositive = true
            )
        )
    }

    private fun liveStatNumericValue(statValue: Any?): Int {
        return when (statValue) {
            is Int -> statValue
            is Double -> statValue.toInt()
            is Float -> statValue.toInt()
            is Boolean -> if (statValue) 1 else 0
            is String -> {
                val trimmed = statValue.trim()
                if (trimmed.contains("%")) {
                    trimmed.replace("%", "")
                        .trim()
                        .toDoubleOrNull()
                        ?.toInt() ?: 0
                } else {
                    trimmed.toDoubleOrNull()?.toInt() ?: 0
                }
            }

            else -> 0
        }
    }

    private fun isLivePositive(statType: LiveStatisticType?): Boolean {
        return when (statType) {
            LiveStatisticType.SHOTS_ON_GOAL,
            LiveStatisticType.SHOTS_INSIDEBOX,
            LiveStatisticType.EXPECTED_GOALS,
            LiveStatisticType.PASSES_ACCURATE,
            LiveStatisticType.BALL_POSSESSION,
            LiveStatisticType.PASSES_PERCENTAGE,
            LiveStatisticType.CORNER_KICKS,
            LiveStatisticType.GOALKEEPER_SAVES,
            LiveStatisticType.GOALS_PREVENTED ->
                true

            LiveStatisticType.SHOTS_OFF_GOAL,
            LiveStatisticType.BLOCKED_SHOTS,
            LiveStatisticType.SHOTS_OUTSIDEBOX,
            LiveStatisticType.FOULS,
            LiveStatisticType.OFFSIDES,
            LiveStatisticType.RED_CARDS,
            LiveStatisticType.YELLOW_CARDS ->
                false

            else ->
                true
        }
    }


    private fun liveTotals(
        statType: LiveStatisticType?,
        totalShots: Int,
        totalOutBox: Int,
        totalInBox: Int,
        totalPasses: Int
    ): Int = when (statType) {
        LiveStatisticType.SHOTS_ON_GOAL,
        LiveStatisticType.SHOTS_OFF_GOAL,
        LiveStatisticType.BLOCKED_SHOTS ->
            totalShots

        LiveStatisticType.SHOTS_INSIDEBOX,
        LiveStatisticType.SHOTS_OUTSIDEBOX ->
            totalInBox + totalOutBox

        LiveStatisticType.EXPECTED_GOALS ->
            5

        LiveStatisticType.PASSES_ACCURATE ->
            totalPasses

        LiveStatisticType.BALL_POSSESSION,
        LiveStatisticType.PASSES_PERCENTAGE ->
            100

        LiveStatisticType.OFFSIDES,
        LiveStatisticType.YELLOW_CARDS ->
            8

        LiveStatisticType.RED_CARDS ->
            4

        LiveStatisticType.GOALKEEPER_SAVES ->
            10

        else ->
            20

    }


}