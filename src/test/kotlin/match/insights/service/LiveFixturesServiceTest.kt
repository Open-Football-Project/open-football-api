package match.insights.service

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import match.insights.apidata.LiveData
import match.insights.apidata.MatchesData
import match.insights.clientData.MatchResponse
import match.insights.data.client.ClientLiveMatches
import match.insights.datamanipulation.DataManipulation
import match.insights.response.LiveHomeAwayForm
import match.insights.live.SSEProvider
import match.insights.model.Poll
import match.insights.model.PollVotingOption
import match.insights.response.HomeAwayTeamLastFive
import match.insights.response.LiveLeagueMatches
import match.insights.response.LiveMatch
import match.insights.response.LiveMatchesResponse
import match.insights.sorting.MatchesSort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter


class LiveFixturesServiceTest {
    val liveData: LiveData = mockk()
    val sseProvider: SSEProvider = mockk()
    val sseEmitter: SseEmitter = mockk()
    val pollsService: PollsService = mockk()
    val matchesSort: MatchesSort = mockk()
    val matchesData: MatchesData = mockk()
    val dataManipulation: DataManipulation = mockk()

    val underTest: LiveFixturesService =
        LiveFixturesService(liveData, matchesSort, sseProvider, pollsService, matchesData, dataManipulation)


    @Test
    fun fetchAllLiveMatches() {
        val poll = Poll(
            pollTitle = "Match Winner",
            "match-winner", 233, listOf(
                PollVotingOption("home", "Home", 2)
            )
        )

        every { liveData.allLiveMatches() } returns ClientLiveMatches.liveFixtures
        every {
            matchesSort.sortByPriorityLeagues<LiveMatch>(
                any(),
                any()
            )
        } returns listOf()

        every {
            matchesSort.sortByPriorityCountries<LiveMatchesResponse>(
                any(),
                any()
            )
        } returns listOf(
            LiveMatchesResponse.fromLiveFixtureResponses(
                "England", listOf(
                    LiveLeagueMatches(
                        39,
                        "Premier League",
                        "England",
                        "",
                        listOf(
                            LiveMatch(
                                1234,
                                "",
                                "",
                                "team a",
                                1223,
                                "team b",
                                2323,
                                3,
                                3,
                                90,
                                2,
                                "Premier League",
                                232324,
                                "2025-02-01T15:30:00Z",
                                "FT",
                                "FULL TIME",
                                listOf(),
                                listOf(poll),
                                homeAwayForm = LiveHomeAwayForm(
                                    emptyList(),
                                    emptyList(),
                                    false,
                                    false,
                                    false,
                                    false,
                                    false
                                )
                            )
                        )
                    )
                )
            )
        )

        every { pollsService.getPolls(any()) } returns listOf(poll)
        every { matchesData.lastFiveMatchesResults(any(), any()) } returns emptyMap<Int, List<MatchResponse>>()
        every { dataManipulation.buildLiveHomeAwayForm(any(), any(), any()) } returns
                LiveHomeAwayForm(
                    emptyList(),
                    emptyList(),
                    isHomeHot = false,
                    isHomeCold = false,
                    isAwayHot = false,
                    isAwayCold = false,
                    isDisplayable = false
                )

        val result = underTest.getLiveMatches()


        assertThat(result[0].country).isEqualTo("England")
        assertThat(result[0].leagueMatches).isNotNull
        assertThat(result[0].leagueMatches[0].matches[0].polls).isEqualTo(listOf(poll))
        assertThat(result[0].leagueMatches[0].matches[0].homeAwayForm).isNotNull

        verify { liveData.allLiveMatches() }
        verify { pollsService.getPolls(any()) }
    }


    @Test
    fun shouldStartLiveMatchesStream() {
        val poll = Poll(
            pollTitle = "Match Winner",
            "match-winner", 233, listOf(
                PollVotingOption("home", "Home", 2)
            )
        )

        every { liveData.allLiveMatches() } returns ClientLiveMatches.liveFixtures
        every { sseEmitter.send(any<List<LiveMatch>>()) } just runs
        every { sseProvider.newEmitter(any()) } returns sseEmitter

        every { pollsService.getPolls(any()) } returns
                listOf(poll)

        val result = underTest.streamLiveMatches()

        assertThat(result).isNotNull


        verify(timeout = 2000) { sseProvider.newEmitter(any()) }

    }
}