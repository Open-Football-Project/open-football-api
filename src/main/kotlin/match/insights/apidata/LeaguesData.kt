package match.insights.apidata

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import match.insights.client.ApiSportsClient
import match.insights.clientData.LeagueAndCountry
import match.insights.clientData.LeagueWithStandings
import match.insights.clientData.MatchResponse
import match.insights.clientData.PlayerResponse
import match.insights.seasons.Seasons
import org.springframework.stereotype.Component


@Component
class LeaguesData(
    private val apiSportsClient: ApiSportsClient,
    private val seasons: Seasons
) {

    fun leagues(): List<LeagueAndCountry> = apiSportsClient.fetchAllLeagues("/leagues?current=true")


    fun leagueStandings(leagueId: Int): LeagueWithStandings? =
        apiSportsClient.fetchLeagueInfo("/standings?league=$leagueId&season=${seasons.leagueCurrentSeason(leagueId)}")


    fun leagueSeasonMatches(leagueId: Int, season: Int? = null): List<MatchResponse> =
        apiSportsClient.fetchMatches("/fixtures/?season=${season ?: seasons.leagueCurrentSeason(leagueId)}&league=${leagueId}")

    fun allLeaguePlayers(leagueId: Int): Map<Int, List<PlayerResponse>> =
        apiSportsClient.fetchPlayers("/players?league=${leagueId}&season=${seasons.leagueCurrentSeason(leagueId)}&page=1")
            .let { res ->
                mapOf(1 to res.response) + leaguePlayersPages(leagueId, res.paging.total)
            }

    fun leagueTeams(leagueId: Int, season: Int?) =
        apiSportsClient.fetchLeagueTeams(
            "/teams?league=${leagueId}&season=${
                season ?: seasons.leagueCurrentSeason(
                    leagueId
                )
            }"
        )

    private fun leaguePlayersPages(
        leagueId: Int,
        totalPages: Int
    ): Map<Int, List<PlayerResponse>> = runBlocking {
        val jobs = (2..totalPages).associateWith { page ->
            async {
                apiSportsClient.fetchPlayers(
                    "/players?league=${leagueId}&season=${seasons.leagueCurrentSeason(leagueId)}&page=$page"
                ).response

            }
        }

        jobs.mapValues { it.value.await() }
    }

}