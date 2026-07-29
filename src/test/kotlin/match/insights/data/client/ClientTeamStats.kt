package match.insights.data.client

import match.insights.clientData.ClientTeamStatistics
import match.insights.clientData.TeamStatsAvg
import match.insights.clientData.TeamStatsBiggest
import match.insights.clientData.TeamStatsCardInfo
import match.insights.clientData.TeamStatsCards
import match.insights.clientData.TeamStatsFixtures
import match.insights.clientData.TeamStatsGoalPeriod
import match.insights.clientData.TeamStatsGoals
import match.insights.clientData.TeamStatsGoalsForAgainst
import match.insights.clientData.TeamStatsGoalsHomeAway
import match.insights.clientData.TeamStatsHomeAwayGoals
import match.insights.clientData.TeamStatsHomeAwayScore
import match.insights.clientData.TeamStatsInfo
import match.insights.clientData.TeamStatsLeagueInfo
import match.insights.clientData.TeamStatsLineup
import match.insights.clientData.TeamStatsOverUnder
import match.insights.clientData.TeamStatsPenalty
import match.insights.clientData.TeamStatsPenaltyStats
import match.insights.clientData.TeamStatsStreak
import match.insights.clientData.TeamStatsTotals

class ClientTeamStats {

    companion object {
        val mockTeamStats = ClientTeamStatistics(
            league = TeamStatsLeagueInfo(128, "Liga Profesional Argentina", "Argentina", null, null, 2025),
            team = TeamStatsInfo(435, "River Plate", "https://media.api-sports.io/football/teams/435.png"),
            form = "DWDWDWWLWDDDDWWWWLWWDDWDWWLLLLWL",
            fixtures = TeamStatsFixtures(
                played = TeamStatsTotals(18, 14, 32),
                wins = TeamStatsTotals(10, 5, 15),
                draws = TeamStatsTotals(3, 7, 10),
                loses = TeamStatsTotals(5, 2, 7)
            ),
            goals = TeamStatsGoals(
                forward = TeamStatsGoalsForAgainst(
                    total = TeamStatsTotals(28, 17, 45),
                    average = TeamStatsAvg("1.6", "1.2", "1.4"),
                    minute = mapOf("0-15" to TeamStatsGoalPeriod(7, "15.56%")),
                    under_over = mapOf("0.5" to TeamStatsOverUnder(23, 9))
                ),
                against = TeamStatsGoalsForAgainst(
                    total = TeamStatsTotals(16, 8, 24),
                    average = TeamStatsAvg("0.9", "0.6", "0.8"),
                    minute = mapOf("0-15" to TeamStatsGoalPeriod(6, "26.09%")),
                    under_over = mapOf("0.5" to TeamStatsOverUnder(17, 15))
                )
            ),
            biggest = TeamStatsBiggest(
                streak = TeamStatsStreak(4, 4, 4),
                wins = TeamStatsHomeAwayScore("4-1", "0-4"),
                loses = TeamStatsHomeAwayScore("0-2", "2-0"),
                goals = TeamStatsGoalsHomeAway(
                    forward = TeamStatsHomeAwayGoals(4, 4),
                    against = TeamStatsHomeAwayGoals(2, 2)
                )
            ),
            clean_sheet = TeamStatsTotals(7, 8, 15),
            failed_to_score = TeamStatsTotals(4, 5, 9),
            penalty = TeamStatsPenalty(
                scored = TeamStatsPenaltyStats(2, "100.00%"),
                missed = TeamStatsPenaltyStats(0, "0%"),
                total = 2
            ),
            lineups = listOf(TeamStatsLineup("4-3-3", 15), TeamStatsLineup("4-3-1-2", 7)),
            cards = TeamStatsCards(
                yellow = mapOf("0-15" to TeamStatsCardInfo(6, "6.32%")),
                red = mapOf("31-45" to TeamStatsCardInfo(2, "40.00%"))
            )
        )
    }
}