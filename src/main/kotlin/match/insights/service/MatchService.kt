package match.insights.service

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import match.insights.apidata.LiveData
import match.insights.apidata.MatchesData
import match.insights.clientData.LiveFixtureResponse
import match.insights.clientData.MatchResponse
import match.insights.datamanipulation.TeamStatisticsManipulation
import match.insights.errors.ApiFailedException
import match.insights.errors.ErrorMessage
import match.insights.internaldata.VideoContentManager
import match.insights.model.ContentKey
import match.insights.model.VideoContent
import match.insights.response.DayMatches
import match.insights.response.DayMatchesResponse
import match.insights.response.MatchDetails
import match.insights.response.MatchEvent
import match.insights.response.TeamsLineups
import match.insights.response.TwoTeamsStatistics
import match.insights.response.isEmpty
import match.insights.sorting.MatchesSort
import org.springframework.stereotype.Service
import kotlin.collections.component1
import kotlin.collections.component2


@Service
class MatchService(
    private val apidata: MatchesData,
    private val liveData: LiveData,
    private val statisticsManipulation: TeamStatisticsManipulation,
    private val matchesSort: MatchesSort,
    private val videoContentManager: VideoContentManager
) {

    fun getMatchesByDate(date: String): List<DayMatchesResponse> {
        val data = getMatchesByDateAndLive(date)

        val dateMatches: List<MatchResponse> = data["matches"] as List<MatchResponse>
        val liveIds = liveMatchesIds(data["live"] as List<LiveFixtureResponse>)

        return sortDayMatchesResponse(groupByCountry(dateMatches, liveIds))
    }


    fun getMatchDetails(matchId: Int): MatchDetails {
        val data = getDetailsAndLiveMatches(matchId)

        val details: MatchResponse = data["details"] as MatchResponse
        val liveIds = liveMatchesIds(data["live"] as List<LiveFixtureResponse>)
        val videoUrls = videoContentManager.getVideos(ContentKey.MATCH, matchId)

        return MatchDetails.fromResponseData(details, liveIds, videoUrls)
    }


    fun matchLineups(fixtureId: Int) = TeamsLineups.fromClientData(apidata.lineups(fixtureId))

    fun matchStats(fixtureId: Int): TwoTeamsStatistics =
        statisticsManipulation.twoTeamsStats(apidata.statistics(fixtureId))
            .let {
                if (it.isEmpty()) throw ApiFailedException(ErrorMessage.EMPTY_STATS)
                else it
            }

    fun matchEvents(fixtureId: Int): List<MatchEvent> = apidata.singleMatchEvents(fixtureId = fixtureId)
        .map { MatchEvent.fromEvent(it) }

    fun addVideoContent(matchId: Int, videos: Set<VideoContent>): Set<VideoContent> =
        videoContentManager.newContent(ContentKey.MATCH, matchId, videos)

    private fun getMatchesByDateAndLive(date: String): Map<String, Any> = runBlocking {
        val jobs = mapOf(
            "matches" to async { apidata.matchesOfTheDay(date) },
            "live" to async { liveData.allLiveMatches() }
        )

        jobs.mapValues { (_, job) -> job.await() }

    }


    private fun getDetailsAndLiveMatches(matchId: Int): Map<String, Any> = runBlocking {
        val jobs = mapOf(
            "details" to async { apidata.matchDetails(matchId) },
            "live" to async { liveData.allLiveMatches() }
        )

        jobs.mapValues { (_, job) -> job.await() }

    }


    private fun liveMatchesIds(fixtures: List<LiveFixtureResponse>): Set<Int> =
        fixtures.map { it.fixture.id }.toSet()

    private fun groupByCountry(response: List<MatchResponse>, liveIds: Set<Int>) = response
        .groupBy { it.league.country }
        .map { (key, value) ->
            DayMatchesResponse.fromCountryMatches(
                key ?: "Unknown", sortLeagueMatches(
                    groupByLeague(value, liveIds)
                )
            )
        }

    private fun groupByLeague(response: List<MatchResponse>, liveIds: Set<Int>) =
        response.groupBy { it.league.id }
            .map { (_, leagueMatches) ->
                DayMatches.fromMatchResponses(leagueMatches, liveIds)
            }

    private fun sortDayMatchesResponse(matches: List<DayMatchesResponse>) =
        matchesSort.sortByPriorityCountries(matches) { it.country }

    private fun sortLeagueMatches(dayMatches: List<DayMatches>) =
        matchesSort.sortByPriorityLeagues(dayMatches) { it.leagueName }


}