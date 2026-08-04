package org.footballproject.service

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.footballproject.apidata.LeaguesData
import org.footballproject.model.ArgLeagueEntry
import org.footballproject.model.MatchStatus
import org.footballproject.response.ArgSpecial
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class ArgentinaService(
    private val leaguesData: LeaguesData

) {
    fun argSpecialTables(): ArgSpecial = ArgSpecial(
        buildAnnualTable(LEAGUE_ID, CURRENT_YEAR),
        computePromedios(listOf(LEAGUE_ID, CUP_LEAGUE_ID), CURRENT_YEAR, PROMEDIOS_TOTAL_SEASONS)
    )

    fun buildAnnualTable(leagueId: Int, season: Int? = null): List<ArgLeagueEntry> {
        val matches = leaguesData.leagueSeasonMatches(leagueId, season)

        val teams = matches.mapNotNull { it.teams.home }.distinctBy { it.id }

        val leagueMatches = matches.filter { match ->
            match.fixture.status?.short == MatchStatus.FULL_TIME.code && match.league.id == leagueId && isRegularLeagueRound(
                match.league.round
            )
        }

        val table = teams.map { team ->
            VariableLeagueEntry(teamId = team.id, teamName = team.name, teamLogo = team.logo ?: "")
        }.associateBy { it.teamId }.toMutableMap()


        leagueMatches.forEach { match ->
            val home = table[match.teams.home?.id] ?: return@forEach
            val away = table[match.teams.away?.id] ?: return@forEach

            val homeGoals = match.goals?.home ?: 0
            val awayGoals = match.goals?.away ?: 0

            home.played += 1
            away.played += 1

            home.goalsFor += homeGoals
            home.goalsAgainst += awayGoals
            away.goalsFor += awayGoals
            away.goalsAgainst += homeGoals

            when {
                homeGoals > awayGoals -> {
                    home.wins += 1
                    home.points += 3
                    away.losses += 1
                }

                homeGoals < awayGoals -> {
                    away.wins += 1
                    away.points += 3
                    home.losses += 1
                }

                else -> {
                    home.draws += 1
                    away.draws += 1
                    home.points += 1
                    away.points += 1
                }
            }
        }

        table.values.forEach { it.goalDifference = it.goalsFor - it.goalsAgainst }

        return table.values.map { immutableLeagueEntry(it) }
            .sortedWith(compareByDescending<ArgLeagueEntry> { it.points }.thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsFor })
    }

    fun computePromedios(
        leagueIds: List<Int>, currentYear: Int, seasonsToInclude: Int = PROMEDIOS_TOTAL_SEASONS
    ): List<ArgLeagueEntry> {

        val currentSeasonTeams = buildAnnualTable(leagueIds.first(), currentYear).map { it.teamId }.toSet()

        val seasons = (0 until seasonsToInclude).map { currentYear - it }

        val allEntries = fetchAnnualTablesParallel(leagueIds, seasons).filter { it.teamId in currentSeasonTeams }

        val promedioMap = allEntries.groupBy { it.teamId }

        return promedioMap.map { (_, entries) ->
            val totalPoints = entries.sumOf { it.points }
            val totalMatches = entries.sumOf { it.played }

            val base = entries.first()

            ArgLeagueEntry(
                teamId = base.teamId,
                teamName = base.teamName,
                teamLogo = base.teamLogo,
                played = totalMatches,
                points = totalPoints,
                wins = entries.sumOf { it.wins },
                draws = entries.sumOf { it.draws },
                losses = entries.sumOf { it.losses },
                goalsFor = entries.sumOf { it.goalsFor },
                goalsAgainst = entries.sumOf { it.goalsAgainst },
                goalDifference = entries.sumOf { it.goalsFor } - entries.sumOf { it.goalsAgainst },
                promedio = if (totalMatches > 0) roundPromedio(totalPoints.toDouble() / totalMatches)
                else 0.0
            )
        }.sortedByDescending { it.promedio }
    }


    private fun isRegularLeagueRound(round: String?): Boolean {
        if (round.isNullOrBlank()) return false

        val normalized = round.lowercase()


        if (knockoutMatchesKeywords.any { normalized.contains(it) }) return false

        return normalized.trim().last().isDigit()
    }

    private fun roundPromedio(value: Double): Double = BigDecimal(value).setScale(3, RoundingMode.HALF_UP).toDouble()


    fun fetchAnnualTablesParallel(
        leagueIds: List<Int>, seasons: List<Int>
    ): List<ArgLeagueEntry> = runBlocking {

        val jobs = leagueIds.flatMap { leagueId ->
            seasons.map { season ->
                async {
                    buildAnnualTable(leagueId, season)
                }
            }
        }

        jobs.flatMap { it.await() }
    }


    private fun immutableLeagueEntry(leagueEntry: VariableLeagueEntry): ArgLeagueEntry = ArgLeagueEntry(
        leagueEntry.teamId,
        leagueEntry.teamName,
        leagueEntry.teamLogo,
        leagueEntry.points,
        leagueEntry.played,
        leagueEntry.wins,
        leagueEntry.draws,
        leagueEntry.losses,
        leagueEntry.goalsFor,
        leagueEntry.goalsAgainst,
        leagueEntry.goalDifference,
        leagueEntry.promedio
    )

    private data class VariableLeagueEntry(
        var teamId: Int = -1,
        var teamName: String = "",
        var teamLogo: String = "",
        var points: Int = 0,
        var played: Int = 0,
        var wins: Int = 0,
        var draws: Int = 0,
        var losses: Int = 0,
        var goalsFor: Int = 0,
        var goalsAgainst: Int = 0,
        var goalDifference: Int = 0,
        var promedio: Double = 0.0
    )

    companion object {
        const val LEAGUE_ID = 128
        const val CUP_LEAGUE_ID = 1032
        val CURRENT_YEAR: Int = LocalDate.now().year
        val knockoutMatchesKeywords = listOf(
            "final", "quarter", "semi", "8th", "round"
        )

        const val PROMEDIOS_TOTAL_SEASONS: Int = 3
    }

}
