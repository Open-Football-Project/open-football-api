package match.insights.service


import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import match.insights.apidata.LeaguesData
import match.insights.apidata.LiveData
import match.insights.data.client.ClientLeagueData
import match.insights.data.client.ClientLiveMatches
import match.insights.data.client.ClientMatchResponseData
import match.insights.data.client.ClientTeamDetails
import match.insights.datamanipulation.FixturesManipulation
import match.insights.datamanipulation.LeagueDataManipulation
import match.insights.internaldata.VideoContentManager
import match.insights.model.ContentKey
import match.insights.model.VideoContent
import match.insights.response.LeagueFixture

import match.insights.response.LeagueInfo
import match.insights.response.LeagueRankings
import match.insights.response.LeaguesGroups
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class LeagueServiceTest {

    val apidata: LeaguesData = mockk()
    val livedata: LiveData = mockk()

    val leagueDataManipulation: LeagueDataManipulation = mockk()
    val fixturesManipulation: FixturesManipulation = mockk()
    val videoContentManager: VideoContentManager = mockk()

    val underTest = LeagueService(apidata, livedata, leagueDataManipulation, fixturesManipulation, videoContentManager)

    @Test
    fun shouldLeagueStanding() {
        every { apidata.leagueStandings(any()) } returns ClientLeagueData.leagueStandings
        every { leagueDataManipulation.extractLeaguesInfo(any()) } returns LeagueInfo(
            id = 1,
            season = 2025,
            group = emptyList(),
            videos = emptySet()
        )
        every { videoContentManager.getVideos(any(), any()) } returns emptySet()

        val result = underTest.leagueInfo(39)

        assertThat(result.id).isEqualTo(1)
        assertThat(result.season).isEqualTo(2025)
        assertThat(result.group).isEmpty()

        verify { apidata.leagueStandings(any()) }
        verify { leagueDataManipulation.extractLeaguesInfo(any()) }
    }

    @Test
    fun shouldIncludeVideoContentInLeagueInfo() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { apidata.leagueStandings(any()) } returns ClientLeagueData.leagueStandings
        every { leagueDataManipulation.extractLeaguesInfo(any()) } returns LeagueInfo(
            id = 1,
            season = 2025,
            group = emptyList(),
            videos = emptySet()
        )
        every { videoContentManager.getVideos(ContentKey.LEAGUE, 39) } returns videos

        val result = underTest.leagueInfo(39)

        assertThat(result.videos).isEqualTo(videos)

        verify { videoContentManager.getVideos(ContentKey.LEAGUE, 39) }
    }

    @Test
    fun shouldAddVideoContentToLeague() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { videoContentManager.newContent(ContentKey.LEAGUE, 39, videos) } returns videos

        val result = underTest.addVideoContent(39, videos)

        assertThat(result).isEqualTo(videos)
        verify { videoContentManager.newContent(ContentKey.LEAGUE, 39, videos) }
    }


    @Test
    fun shouldFetchAllLeagues() {
        every { apidata.leagues() } returns ClientLeagueData.allLeagues
        every { leagueDataManipulation.groupLeagues(any()) } returns LeaguesGroups(
            internationals = listOf(),
            countryLeagues = listOf(),
            others = listOf()
        )

        assertThat(underTest.allLeagues()).isNotNull


        verify { apidata.leagues() }
        verify { leagueDataManipulation.groupLeagues(any()) }
    }

    @Test
    fun shouldFetchTheLeagueFixture() {
        every { apidata.leagueSeasonMatches(1) } returns ClientMatchResponseData.matchResponseList
        every { livedata.allLiveMatches() } returns ClientLiveMatches.liveFixtures
        every { fixturesManipulation.toLeagueFixture(any(), any()) } returns LeagueFixture(listOf(), 0, 0)

        val result = underTest.leagueSeasonFixture(1)

        assertThat(result).isNotNull

        verify { apidata.leagueSeasonMatches(1) }
        verify { fixturesManipulation.toLeagueFixture(any(), any()) }
    }

    @Test
    fun shouldFetchTheLeagueRankings() {
        every { apidata.allLeaguePlayers(1) } returns mapOf(1 to ClientTeamDetails.mockPlayersResponse)
        every { leagueDataManipulation.topNRankings(any()) } returns LeagueRankings()

        val result = underTest.leagueRankings(1)

        assertThat(result).isNotNull

        verify { apidata.allLeaguePlayers(1) }
        verify { leagueDataManipulation.topNRankings(any()) }

    }

    @Test
    fun shouldFetchCurrentYearTeams() {
        every { apidata.leagueTeams(128, any()) } returns listOf(ClientTeamDetails.details)

        val result = underTest.currentYearTeams(128)

        assertThat(result).isNotEmpty

        verify { apidata.leagueTeams(128, any()) }

    }

}