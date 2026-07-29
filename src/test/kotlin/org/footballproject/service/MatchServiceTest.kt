package org.footballproject.service

import org.footballproject.data.client.ClientMatchResponseData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.apidata.LiveData
import org.footballproject.apidata.MatchesData
import org.footballproject.data.client.ClientEventsData
import org.footballproject.data.client.ClientLineupResponseData
import org.footballproject.data.client.ClientLiveMatches
import org.footballproject.data.client.ClientStatistics
import org.footballproject.datamanipulation.TeamStatisticsManipulation
import org.footballproject.response.DayMatches
import org.footballproject.response.DayMatchesResponse
import org.footballproject.response.TeamStats
import org.footballproject.response.TeamsLineups
import org.footballproject.response.TeamStatistic
import org.footballproject.response.TwoTeamsStatistics
import org.footballproject.sorting.MatchesSort
import org.footballproject.errors.ApiFailedException
import org.footballproject.internaldata.VideoContentManager
import org.footballproject.model.ContentKey
import org.footballproject.model.VideoContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MatchServiceTest {

    val apidata: MatchesData = mockk()
    val liveData: LiveData = mockk()
    val matchesSort: MatchesSort = mockk()
    val statisticsManipulation: TeamStatisticsManipulation = mockk()
    val videoContentManager: VideoContentManager = mockk()
    val underTest = MatchService(apidata, liveData, statisticsManipulation, matchesSort, videoContentManager)

    @Test
    fun shouldGetTodayMatches() {
        every { liveData.allLiveMatches() } returns ClientLiveMatches.liveFixtures
        every { apidata.matchesOfTheDay(any()) } returns listOf(ClientMatchResponseData.matchResponse)
        every {
            matchesSort.sortByPriorityLeagues<DayMatches>(
                any(),
                any()
            )
        } returns listOf()

        every {
            matchesSort.sortByPriorityCountries<DayMatchesResponse>(
                any(),
                any()
            )
        } returns listOf(
            DayMatchesResponse.fromCountryMatches(
                "England", listOf()
            )
        )


        val result = underTest.getMatchesByDate("2024-10-10")

        assertThat(result[0].country).isEqualTo("England")
        assertThat(result[0]).isNotNull

        verify { apidata.matchesOfTheDay(any()) }
    }

    @Test
    fun shouldGetMatchDetails() {
        every { liveData.allLiveMatches() } returns ClientLiveMatches.liveFixtures
        every { apidata.matchDetails(any()) } returns ClientMatchResponseData.matchResponse
        every { videoContentManager.getVideos(any(), any()) } returns emptySet()

        val match = underTest.getMatchDetails(1234)

        assertThat(match.id).isNotNull
        assertThat(match.score).isNotNull
        assertThat(match.goals).isNotNull
        assertThat(match.videos).isEmpty()

        verify { apidata.matchDetails(any()) }
        verify { videoContentManager.getVideos(any(), any()) }
    }

    @Test
    fun shouldIncludeVideoUrlsInMatchDetailsWhenAvailable() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { liveData.allLiveMatches() } returns ClientLiveMatches.liveFixtures
        every { apidata.matchDetails(any()) } returns ClientMatchResponseData.matchResponse
        every { videoContentManager.getVideos(any(), any()) } returns videos

        val match = underTest.getMatchDetails(1234)

        assertThat(match.videos).isEqualTo(videos)

        verify { videoContentManager.getVideos(any(), any()) }
    }

    @Test
    fun shouldAddNewVideoContent() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )

        every { videoContentManager.newContent(ContentKey.MATCH, 1089175, videos) } returns videos

        val result = underTest.addVideoContent(1089175, videos)

        assertThat(result).isEqualTo(videos)
        verify { videoContentManager.newContent(ContentKey.MATCH, 1089175, videos) }
    }


    @Test
    fun shouldFetchLiveFixturesStats() {
        every { apidata.statistics(any()) } returns ClientStatistics.liveStatistics
        every { statisticsManipulation.twoTeamsStats(ClientStatistics.liveStatistics) } returns TwoTeamsStatistics(
            TeamStats(statistics = listOf(TeamStatistic("Shots on Goal", 7, 14, true))),
            TeamStats(statistics = listOf(TeamStatistic("Shots on Goal", 4, 12, true)))
        )

        val result = underTest.matchStats(2324)

        assertThat(result).isInstanceOf(TwoTeamsStatistics::class.java)

        verify { apidata.statistics(any()) }
    }

    @Test
    fun shouldThrowExceptionWhenStatsAreEmpty() {
        every { apidata.statistics(any()) } returns ClientStatistics.liveStatistics
        every { statisticsManipulation.twoTeamsStats(any()) } returns TwoTeamsStatistics(
            TeamStats(statistics = listOf()),
            TeamStats()
        )

        assertThrows<ApiFailedException> {
            underTest.matchStats(2324)
        }

        verify { apidata.statistics(any()) }
    }


    @Test
    fun shouldFetchLiveTeamLineups() {
        every { apidata.lineups(any()) } returns ClientLineupResponseData.mockLineups

        val result = underTest.matchLineups(2324)

        assertThat(result).isInstanceOf(TeamsLineups::class.java)

        verify { apidata.lineups(any()) }
    }


    @Test
    fun shouldFetchMatchEvents() {
        every { apidata.singleMatchEvents(any()) } returns ClientEventsData.mockEvents

        val result = underTest.matchEvents(2334)

        assertThat(result).isNotEmpty

        verify { apidata.singleMatchEvents(any()) }
    }
}