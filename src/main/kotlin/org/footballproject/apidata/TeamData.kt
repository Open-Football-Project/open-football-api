package org.footballproject.apidata

import org.footballproject.client.ApiSportsClient

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.footballproject.clientData.ClientTeamStatistics
import org.footballproject.clientData.PlayerResponse
import org.footballproject.clientData.PlayerTransfer
import org.footballproject.clientData.SquadPlayer
import org.footballproject.seasons.Seasons
import org.springframework.stereotype.Component

@Component
class TeamData(
    private val apiSportsClient: ApiSportsClient,
    private val seasons: Seasons
) {

    fun getTeamsDetails(teamId: Int) = runBlocking {
        val jobs = mapOf(
            "details" to async { apiSportsClient.fetchTeamDetails("/teams?id=$teamId") },
            "coach" to async {
                apiSportsClient.fetchCoachDetails("/coachs?team=$teamId")
                    .firstOrNull { coach ->
                        coach.career?.any { it.team?.id == teamId && it.end == null } == true
                    }
            }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }

    fun currentTeamSquad(teamId: Int): List<SquadPlayer> =
        apiSportsClient.fetchCurrentSquad("/players/squads?team=$teamId")


    fun teamSquad(teamId: Int, season: Int? = null): Map<Int, List<PlayerResponse>> =
        apiSportsClient.fetchPlayers("/players?team=$teamId&season=${season ?: seasons.leagueCurrentSeason()}&page=1")
            .let { res ->
                mapOf(1 to res.response) + teamSquadPages(teamId, 2, res.paging.total)
            }


    private fun teamSquadPages(
        teamId: Int,
        nextPage: Int,
        totalPages: Int,
        season: Int? = null
    ): Map<Int, List<PlayerResponse>> = runBlocking {
        val jobs = (nextPage..totalPages).associateWith { page ->
            async {
                apiSportsClient.fetchPlayers(
                    "/players?team=$teamId&season=${season ?: seasons.leagueCurrentSeason()}&page=$page"
                ).response

            }
        }
        jobs.mapValues { it.value.await() }
    }


    fun transfers(teamId: Int): List<PlayerTransfer> =
        apiSportsClient.fetchTransfers("/transfers?team=$teamId")

    fun teamStats(teamId: Int, season: Int? = null, leagueId: Int): ClientTeamStatistics =
        apiSportsClient.fetchTeamStats(
            "/teams/statistics?team=$teamId&season=${
                season ?: seasons.leagueCurrentSeason(
                    leagueId
                )
            }&league=$leagueId"
        )


    fun getTwoTeamsStats(homeTeamId: Int, awayTeamId: Int, leagueId: Int) = runBlocking {
        val jobs = mapOf(
            "hometeamstats" to async { teamStats(homeTeamId, seasons.leagueCurrentSeason(leagueId), leagueId) },
            "awayteamstats" to async { teamStats(awayTeamId, seasons.leagueCurrentSeason(leagueId), leagueId) },

            )
        jobs.mapValues { (_, job) -> job.await() }
    }

    fun teamLeagues(teamId: Int, season: Int) =
        apiSportsClient.fetchTeamLeagues("/leagues?team=${teamId}&season=${season}")
}