package org.footballproject.service

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.footballproject.apidata.LeaguesData
import org.footballproject.apidata.TeamData
import org.footballproject.clientData.ClientTeamStatistics
import org.footballproject.clientData.PlayerResponse
import org.footballproject.clientData.TeamResponse
import org.footballproject.response.game.GuessTheTeamGameData
import org.footballproject.response.game.GuessTheTeamGameHint
import org.footballproject.response.game.GuessThetTeamGameHintDescriptionKey
import org.footballproject.response.game.GuessThetTeamGameHintKey

import org.springframework.stereotype.Service
import java.time.Year
import kotlin.collections.any
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.isNotEmpty

@Service
class TeamGameService(
    private val teamData: TeamData,
    private val leaguesData: LeaguesData
) {
    private val worldCupLeagueId = 1
    private val clubWorldCupLeagueId = 1
    fun newTeamGame(leagueId: Int): GuessTheTeamGameData {
        val anyYear =
            if (leagueId == worldCupLeagueId || leagueId == clubWorldCupLeagueId) randomWorldCupsYear(leagueId) else randomYear()

        val saferYear = safeRandomYear()

        return getTeams(leagueId, anyYear, saferYear)
            .let { teamsPair ->
                if (teamsPair.first == -1) GuessTheTeamGameData()
                else {
                    val season = teamsPair.first
                    val randomTeam = teamsPair.second.random()

                    GuessTheTeamGameData(
                        isAvailable = true,
                        teamId = randomTeam.team.id,
                        teamName = randomTeam.team.name,
                        teamLogo = randomTeam.team.logo,
                        venue = randomTeam.venue.name ?: "Unknown",
                        founded = randomTeam.team.founded ?: -1,
                        season = season,
                        hints = generateHints(season, randomTeam.team.id, leagueId),
                        options = generateOptions(randomTeam.team.name, teamsPair.second)
                    )
                }
            }
    }

    fun generateHints(season: Int, teamId: Int, leagueId: Int): List<GuessTheTeamGameHint> {
        val hintsData = findSquadAndStats(teamId, season, leagueId)

        val squad = hintsData["squad"]?.let { it as List<PlayerResponse> } ?: emptyList()
        val stats = hintsData["stats"]?.let { it as ClientTeamStatistics }

        val playerHints =
            if (squad.isNotEmpty()) generatePlayerHints(squad) else emptyList()

        val statsHints =
            if (stats != null) generateStatsHints(stats) else emptyList()

        return (playerHints + statsHints).shuffled().take(5)
    }

    fun generatePlayerHints(players: List<PlayerResponse>): List<GuessTheTeamGameHint> {
        val topPlayers = players
            .filter {
                isAnInterestingPlayer(it)
            }

        return if (topPlayers.isNotEmpty())
            topPlayers.shuffled().take(5).map {
                GuessTheTeamGameHint(
                    GuessThetTeamGameHintKey.PLAYER,
                    GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_TOP_PLAYER,
                    "${it.player.firstname} ${it.player.lastname}"
                )
            }
        else emptyList()
    }

    fun generateStatsHints(stats: ClientTeamStatistics): List<GuessTheTeamGameHint> {
        val scored =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_GOALS_SCORED, stats.goals.forward.total.total ?: 0)
        val conceded =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_GOALS_CONCEDED, stats.goals.against.total.total ?: 0)
        val cleanSheetHome =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_CLEAN_SHEETS_HOME, stats.clean_sheet.home ?: 0)
        val cleanSheetAway =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_CLEAN_SHEETS_AWAY, stats.clean_sheet.away ?: 0)
        val failedToScore =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_FAILED_TO_SCORE, stats.failed_to_score.total ?: 0)

        val totalWon = Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_MATCHES_WON, stats.fixtures.wins.total ?: 0)
        val totalLost =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_MATCHES_LOST, stats.fixtures.loses.total ?: 0)

        val bestMinute = stats.goals.forward.minute.maxByOrNull { it.value?.total ?: 0 }?.key?.let {
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_BEST_MINUTE, it)
        } ?: Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_BEST_MINUTE, "25 - 60")

        val longestWinningStreak = stats.biggest.streak.wins?.let {
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_LONGEST_WINNING_STREAK, it)
        } ?: Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_LONGEST_WINNING_STREAK, 0)

        val totalYellowCards = Pair(
            GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_YELLOW_CARDS,
            stats.cards.yellow.values.sumOf { it.total ?: 0 })

        val totalRedCards = Pair(
            GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_RED_CARDS,
            stats.cards.red.values.sumOf { it.total ?: 0 })


        val totalPenaltiesScored =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_PENALTIES_SCORED, stats.penalty.scored.total ?: 0)

        val totalPenaltiesMissed =
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_PENALTIES_MISSED, stats.penalty.missed.total ?: 0)

        val mostUsedFormation = stats.lineups.maxByOrNull { it.played }?.formation?.let {
            Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_MOST_USED_FORMATION, it)
        } ?: Pair(GuessThetTeamGameHintDescriptionKey.TEAM_QUIZ_MOST_USED_FORMATION, "4-4-2")

        return listOf(
            scored, conceded, cleanSheetHome, cleanSheetAway, failedToScore, totalWon,
            totalLost, bestMinute, longestWinningStreak, totalYellowCards, totalRedCards,
            totalPenaltiesScored, totalPenaltiesMissed, mostUsedFormation
        ).map { GuessTheTeamGameHint(GuessThetTeamGameHintKey.STAT, it.first, it.second.toString()) }


    }

    fun generateOptions(teamName: String, teams: List<TeamResponse>): List<String> {
        val options = teams.filter { it.team.name != teamName }.take(3).map { it.team.name }

        return (options + listOf(teamName)).shuffled()
    }

    fun isAnInterestingPlayer(player: PlayerResponse): Boolean {
        return player.statistics.any { stat ->
            val games = stat.games
            val goals = stat.goals
            val cards = stat.cards
            val penalty = stat.penalty

            ((games.appearences ?: 0) >= 50) ||
                    ((games.minutes ?: 0) >= 1500) ||
                    ((goals.total ?: 0) >= 5) ||
                    ((goals.assists ?: 0) >= 5) ||
                    ((cards.yellow ?: 0) >= 6) ||
                    ((cards.red ?: 0) >= 2) ||
                    ((penalty.scored ?: 0) >= 3) ||
                    ((penalty.saved ?: 0) >= 3)
        }
    }

    fun getTeams(leagueId: Int, year: Int, safeYear: Int): Pair<Int, List<TeamResponse>> =
        findTeamsData(leagueId, year, safeYear).let {
            if (it[year]?.isNotEmpty() == true) return Pair(year, it[year] ?: emptyList())
            if (it[safeYear]?.isNotEmpty() == true) return Pair(safeYear, it[safeYear] ?: emptyList())
            return Pair(-1, emptyList())
        }


    private fun findSquadAndStats(teamId: Int, season: Int, leagueId: Int) = runBlocking {
        val jobs = mapOf(
            "squad" to async { teamData.teamSquad(teamId, season).flatMap { it.value } },
            "stats" to async { teamData.teamStats(teamId, season, leagueId) }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }

    private fun findTeamsData(leagueId: Int, year: Int, safeYear: Int) = runBlocking {
        val jobs = mapOf(
            year to async { leaguesData.leagueTeams(leagueId, year) },
            safeYear to async { leaguesData.leagueTeams(leagueId, safeYear) }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }


    private fun randomYear(): Int =
        (1990..Year.now().value).random()

    private fun safeRandomYear(): Int =
        (2015..Year.now().value).random()

    private fun randomWorldCupsYear(leagueId: Int): Int {
        val currentYear = Year.now().value
        val startingYear = if (leagueId == 1) 2010 else 2025

        val validYears = (startingYear..currentYear step 4)
            .filterNot { it > currentYear }
            .toList()

        return validYears.random()
    }

}