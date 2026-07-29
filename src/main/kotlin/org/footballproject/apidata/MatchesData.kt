package org.footballproject.apidata

import org.footballproject.client.ApiSportsClient
import org.footballproject.clientData.Event
import org.footballproject.clientData.MatchResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.footballproject.model.MatchStatus
import org.footballproject.seasons.Seasons
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Component
class MatchesData(
    private val apiSportsClient: ApiSportsClient,
    private val seasons: Seasons,

    ) {

    fun matchesOfTheDay(day: String): List<MatchResponse> =
        apiSportsClient.fetchMatches("/fixtures?date=$day")


    fun matchDetails(matchId: Int): MatchResponse =
        apiSportsClient.fetchMatchDetails("/fixtures?id=${matchId}")


    fun lastFiveMatchesResults(homeTeamId: Int, awayTeamId: Int): Map<Int, List<MatchResponse>> = runBlocking {
        val jobs = mapOf(
            homeTeamId to async { lastFiveMatches(homeTeamId) },
            awayTeamId to async { lastFiveMatches(awayTeamId) }
        )

        jobs.mapValues { (_, job) -> job.await() }

    }

    fun getTeamsLeagueMatches(homeTeamId: Int, awayTeamId: Int, leagueId: Int) = runBlocking {
        val jobs = mapOf(
            homeTeamId to async { teamOnLeagueMatches(homeTeamId, leagueId) },
            awayTeamId to async { teamOnLeagueMatches(awayTeamId, leagueId) }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }


    fun lastFiveMatchesEvents(teamId: Int): List<Event> = runBlocking {
        val matches = lastFiveMatches(teamId)

        val deferredEvents = matches.map { match ->
            async {
                apiSportsClient.fetchMatchEvents("/fixtures/events?fixture=${match.fixture.id}")
            }
        }

        val eventsList: List<List<Event>> = deferredEvents.awaitAll()
        eventsList.flatten().filter { it.team.id == teamId }
    }

    fun singleMatchEvents(fixtureId: Int): List<Event> =
        apiSportsClient.fetchMatchEvents("/fixtures/events?fixture=${fixtureId}")


    fun mostRecentPlayedMatches(homeTeamId: Int, awayTeamId: Int) = runBlocking {
        val jobs = mapOf(
            homeTeamId to async { lastFiveMatches(homeTeamId).first() },
            awayTeamId to async { lastFiveMatches(awayTeamId).first() }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }

    fun previousAndUpcomingMatches(teamId: Int) = runBlocking {
        val jobs = mapOf(
            "previous" to async { previousTeamMatches(teamId) },
            "upcoming" to async { upcomingTeamMatches(teamId) }
        )
        jobs.mapValues { (_, job) -> job.await() }
    }


    fun headToHead(homeTeamId: Int, awayTeamId: Int): List<MatchResponse> =
        apiSportsClient.fetchMatches("/fixtures/headtohead?h2h=${homeTeamId}-${awayTeamId}")
            .filter { it.fixture.status?.short == MatchStatus.FULL_TIME.code }
            .sortDescendingByDate()

    fun statistics(fixtureId: Int) =
        apiSportsClient.fetchLiveStatistics("/fixtures/statistics?fixture=${fixtureId}")


    fun lineups(fixtureId: Int) = apiSportsClient.fetchLiveLineups("/fixtures/lineups?fixture=${fixtureId}")

    private fun lastFiveMatches(teamId: Int): List<MatchResponse> =
        apiSportsClient.fetchMatches("/fixtures?team=${teamId}&season=${seasons.leagueCurrentSeason()}")
            .filter { it.fixture.status?.short == MatchStatus.FULL_TIME.code }
            .sortDescendingByDate()
            .take(5)


    private fun teamOnLeagueMatches(teamId: Int, leagueId: Int): List<MatchResponse> =
        apiSportsClient.fetchMatches("/fixtures/?team=${teamId}&season=${seasons.leagueCurrentSeason(leagueId)}&league=${leagueId}")


    private fun List<MatchResponse>.sortDescendingByDate(): List<MatchResponse> {
        return this.sortedByDescending {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            runCatching { ZonedDateTime.parse(it.fixture.date, formatter) }.getOrNull()
        }
    }

    private fun previousTeamMatches(teamId: Int): List<MatchResponse> =
        apiSportsClient.fetchMatches("/fixtures?team=${teamId}&last=10&season=${seasons.leagueCurrentSeason()}")


    private fun upcomingTeamMatches(teamId: Int): List<MatchResponse> =
        apiSportsClient.fetchMatches("/fixtures?team=${teamId}&next=10&season=${seasons.leagueCurrentSeason()}")

}