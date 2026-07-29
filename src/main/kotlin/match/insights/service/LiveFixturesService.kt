package match.insights.service

import match.insights.apidata.LiveData
import match.insights.apidata.MatchesData
import match.insights.clientData.LiveFixtureResponse
import match.insights.datamanipulation.DataManipulation
import match.insights.live.SSEProvider
import match.insights.response.LiveLeagueMatches
import match.insights.response.LiveMatchInfo
import match.insights.response.LiveMatchesResponse
import match.insights.sorting.MatchesSort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import kotlin.collections.component1
import kotlin.collections.component2

@Service
class LiveFixturesService(
    private val liveData: LiveData,
    private val matchesSort: MatchesSort,
    private val sseProvider: SSEProvider,
    private val pollsService: PollsService,
    private val matchesData: MatchesData,
    private val dataManipulation: DataManipulation,
) {

    private val logger = LoggerFactory.getLogger(LiveFixturesService::class.java)

    fun getLiveMatches(): List<LiveMatchesResponse> = matchesSort.sortByPriorityCountries(
        liveData.allLiveMatches()
            .groupBy { it.league.country }
            .toLiveMatchesResponses()) { it.country }


    fun streamLiveMatches(): SseEmitter = sseProvider.newEmitter { getLiveMatches() }


    private fun wrapMatchInfo(matches: List<LiveFixtureResponse>): List<LiveMatchInfo> =
        matches.map {
            LiveMatchInfo(it, pollsService.getPolls(it.fixture.id))
        }


    private fun Map<String?, List<LiveFixtureResponse>>.toLiveMatchesResponses(): List<LiveMatchesResponse> {
        return this.map { (key, value) ->
            LiveMatchesResponse.fromLiveFixtureResponses(
                key ?: "Unknown",
                matchesSort.sortByPriorityLeagues(
                    value.groupBy { it.league.id }
                        .map { (_, leagueMatches) ->
                            LiveLeagueMatches.fromLiveData(wrapMatchInfo(leagueMatches)) { homeId, awayId ->
                                val matches = matchesData.lastFiveMatchesResults(homeId, awayId)
                                dataManipulation.buildLiveHomeAwayForm(homeId, awayId, matches)
                            }
                        }
                ) { it.leagueName }
            )
        }
    }


}