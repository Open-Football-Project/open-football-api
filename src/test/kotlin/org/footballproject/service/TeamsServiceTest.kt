package org.footballproject.service

import org.footballproject.apidata.TeamData
import org.footballproject.datamanipulation.DataManipulation
import org.footballproject.datamanipulation.EventsDataManipulation
import org.footballproject.data.client.ClientEventsData
import org.footballproject.data.client.ClientMatchResponseData
import org.footballproject.data.client.ClientLeagueData
import org.footballproject.data.client.ClientTeamDetails
import org.footballproject.datamanipulation.PerformanceDataManipulation
import org.footballproject.model.Performance
import org.footballproject.model.TeamRestStatus
import org.footballproject.response.LastFiveMatchesEvents
import org.footballproject.response.TeamDetails
import org.footballproject.response.TeamsScorePerformance

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.footballproject.apidata.LeaguesData
import org.footballproject.apidata.LiveData
import org.footballproject.apidata.MatchesData
import org.footballproject.data.client.ClientLiveMatches
import org.footballproject.data.client.ClientSquadPlayers
import org.footballproject.data.client.ClientTeamLeaguesParticipation
import org.footballproject.data.client.ClientTeamStats
import org.footballproject.datamanipulation.FixturesManipulation
import org.footballproject.datamanipulation.LeagueDataManipulation
import org.footballproject.datamanipulation.TeamSquadManipulation
import org.footballproject.datamanipulation.TeamStatisticsManipulation
import org.footballproject.internaldata.VideoContentManager
import org.footballproject.model.ContentKey
import org.footballproject.model.VideoContent
import org.footballproject.response.PlayerSummary
import org.footballproject.response.PositionAndPoints
import org.footballproject.response.TeamFixture
import org.footballproject.response.TeamPlayer
import org.footballproject.response.TeamPositionsAndPoints
import org.footballproject.response.TeamStats
import org.footballproject.response.TwoTeamsStatistics
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.footballproject.errors.ApiFailedException
import kotlin.collections.mapOf

class TeamsServiceTest {

    val apidata: TeamData = mockk()
    val livedata: LiveData = mockk()
    val matchesData: MatchesData = mockk()
    val leaguesData: LeaguesData = mockk()
    val dataManitupulation: DataManipulation = mockk()
    val eventsDataManitupulation: EventsDataManipulation = mockk()
    val performanceDataManipulation: PerformanceDataManipulation = mockk()
    val teamSquadManipulation: TeamSquadManipulation = mockk()
    val leagueDataManipulation: LeagueDataManipulation = mockk()
    val fixturesManipulation: FixturesManipulation = mockk()
    val teamStatisticsManipulation = TeamStatisticsManipulation()
    val videoContentManager: VideoContentManager = mockk()

    val underTest = TeamsService(
        apidata,
        matchesData,
        leaguesData,
        livedata,
        dataManitupulation,
        eventsDataManitupulation,
        performanceDataManipulation,
        teamSquadManipulation,
        leagueDataManipulation,
        fixturesManipulation,
        teamStatisticsManipulation,
        videoContentManager
    )

    @Test
    fun shouldGetLastFiveMatches() {
        every { matchesData.lastFiveMatchesResults(34, 44) } returns mapOf(
            34 to listOf(ClientMatchResponseData.matchResponse), 44 to listOf(ClientMatchResponseData.matchResponse)
        )

        every { dataManitupulation.lastFiveResults(any(), any()) } returns listOf("W", "L", "D", "W", "L")

        val matches = underTest.getLast5MatchesResults(34, 44)

        assertThat(matches.awayTeamLastFive[0]).isEqualTo("W")
        assertThat(matches.awayTeamLastFive[1]).isEqualTo("L")
        assertThat(matches.awayTeamLastFive[2]).isEqualTo("D")
        assertThat(matches.awayTeamLastFive[3]).isEqualTo("W")
        assertThat(matches.awayTeamLastFive[4]).isEqualTo("L")

        verify { matchesData.lastFiveMatchesResults(34, 44) }

    }

    @Test
    fun shouldGetHead2HeadInfo() {
        every { matchesData.headToHead(34, 44) } returns listOf(ClientMatchResponseData.matchResponse)

        val h2hs = underTest.getHeadToHead(34, 44)

        assertThat(h2hs[0].winner).isNotNull
        assertThat(h2hs[0].date).isNotNull

        verify { matchesData.headToHead(34, 44) }

    }

    @Test
    fun shouldGetTeamsStats() {
        every { apidata.getTwoTeamsStats(34, 44, 1) } returns
                mapOf(
                    "hometeamstats" to ClientTeamStats.mockTeamStats,
                    "awayteamstats" to ClientTeamStats.mockTeamStats
                )

        val result = underTest.getTeamsStats(34, 44, 1)

        assertThat(result).isInstanceOfAny(TwoTeamsStatistics::class.java)

        verify { apidata.getTwoTeamsStats(34, 44, 1) }

    }

    @Test
    fun shouldThrowWhenStatsAreEmpty() {
        every { apidata.getTwoTeamsStats(34, 44, 1) } returns mapOf()

        assertThrows<ApiFailedException> {
            underTest.getTeamsStats(34, 44, 1)
        }

        verify { apidata.getTwoTeamsStats(34, 44, 1) }
    }

    @Test
    fun shouldGetASingleTeamStats() {
        every { apidata.teamStats(34, null, 44) } returns
                ClientTeamStats.mockTeamStats

        val result = underTest.getTeamStats(34, 44)

        assertThat(result).isInstanceOfAny(TeamStats::class.java)

        verify { apidata.teamStats(34, null, 44) }

    }


    @Test
    fun shouldGetTeamsPositionsAndPoints() {
        every { leaguesData.leagueStandings(1) } returns ClientLeagueData.leagueStandings
        every {
            leagueDataManipulation.positionAndPoints(
                33,
                44,
                any()
            )
        } returns TeamPositionsAndPoints(
            listOf(PositionAndPoints(1, 15, "")),
            listOf(PositionAndPoints(2, 11, ""))
        )

        val result = underTest.getTeamsPositionsAndPoints(33, 44, 1)

        assertThat(result.awayTeam).isEqualTo(listOf(PositionAndPoints(2, 11, "")))
        assertThat(result.homeTeam).isEqualTo(listOf(PositionAndPoints(1, 15, "")))


        verify { leaguesData.leagueStandings(1) }
        verify { leagueDataManipulation.positionAndPoints(33, 44, any()) }
    }

    @Test
    fun shouldGetTheSumOfTheLastFiveMatchesEvents() {
        val info = LastFiveMatchesEvents(1, 2, 3, 4, 5, 0, 0, 0, 0, 0)
        every {
            matchesData.lastFiveMatchesEvents(1234)
        } returns ClientEventsData.mockEvents
        every {
            eventsDataManitupulation.fiveMachesEventsSum(ClientEventsData.mockEvents)
        } returns info


        val result = underTest.getLast5MatchesEvents(1234)

        assertThat(result).isEqualTo(info)

        verify { matchesData.lastFiveMatchesEvents(1234) }
        verify { eventsDataManitupulation.fiveMachesEventsSum(ClientEventsData.mockEvents) }
    }

    @Test
    fun shouldGetTeamRestStatuses() {
        every { matchesData.mostRecentPlayedMatches(55, 33) } returns mapOf(
            55 to ClientMatchResponseData.matchResponse, 33 to ClientMatchResponseData.matchResponse
        )

        every { dataManitupulation.teamRestStatus(any()) } returns TeamRestStatus.GOOD_REST.status
        every { dataManitupulation.daysBetween(any(), any()) } returns 5

        val result = underTest.teamRestStatuses(55, 33, "2025-09-22T16:30:00+00:00")

        assertThat(result.homeTeamStatus).isEqualTo(TeamRestStatus.GOOD_REST.status)
        assertThat(result.awayTeamStatus).isEqualTo(TeamRestStatus.GOOD_REST.status)

        verify { matchesData.mostRecentPlayedMatches(55, 33) }
        verify { dataManitupulation.teamRestStatus(any()) }
        verify { dataManitupulation.daysBetween(any(), any()) }
    }

    @Test
    fun shouldGetTeamsScorePerformance() {
        every { matchesData.getTeamsLeagueMatches(34, 43, 1) } returns mapOf(
            34 to listOf(ClientMatchResponseData.matchResponse), 43 to listOf(ClientMatchResponseData.matchResponse)
        )

        every {
            performanceDataManipulation.calculateScorePerformance(
                any(), any()
            )
        } returns Performance.GOOD.value

        val result: TeamsScorePerformance = underTest.teamsScorePerformance(34, 43, 1)

        assertThat(result.homeTeamPerformance).isEqualTo(Performance.GOOD.value)
        assertThat(result.awayTeamPerformance).isEqualTo(Performance.GOOD.value)

        verify { matchesData.getTeamsLeagueMatches(34, 43, 1) }
        verify {
            performanceDataManipulation.calculateScorePerformance(
                any(), any()
            )
        }

    }

    @Test
    fun shouldGiveMeTheTeamDetails() {
        every { apidata.getTeamsDetails(22) } returns mapOf(
            "details" to ClientTeamDetails.details, "coach" to ClientTeamDetails.coach
        )
        every { videoContentManager.getVideos(any(), any()) } returns emptySet()

        val result: TeamDetails = underTest.teamDetails(22)

        assertThat(result.coachName).isEqualTo(ClientTeamDetails.coach.name)
        assertThat(result.coachAge).isEqualTo(ClientTeamDetails.coach.age)
        assertThat(result.venueCapacity).isEqualTo(ClientTeamDetails.details.venue.capacity)
        assertThat(result.venueCity).isEqualTo(ClientTeamDetails.details.venue.city)
        assertThat(result.venueName).isEqualTo(ClientTeamDetails.details.venue.name)
        assertThat(result.teamCountry).isEqualTo(ClientTeamDetails.details.team.country)
        assertThat(result.teamName).isEqualTo(ClientTeamDetails.details.team.name)
        assertThat(result.teamLogo).isEqualTo(ClientTeamDetails.details.team.logo)
        assertThat(result.videos).isEmpty()

        verify { apidata.getTeamsDetails(22) }
        verify { videoContentManager.getVideos(ContentKey.TEAM, 22) }
    }

    @Test
    fun shouldIncludeVideoContentInTeamDetails() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { apidata.getTeamsDetails(22) } returns mapOf(
            "details" to ClientTeamDetails.details, "coach" to ClientTeamDetails.coach
        )
        every { videoContentManager.getVideos(ContentKey.TEAM, 22) } returns videos

        val result: TeamDetails = underTest.teamDetails(22)

        assertThat(result.videos).isEqualTo(videos)

        verify { videoContentManager.getVideos(ContentKey.TEAM, 22) }
    }

    @Test
    fun shouldAddVideoContentToTeam() {
        val videos = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { videoContentManager.newContent(ContentKey.TEAM, 22, videos) } returns videos

        val result = underTest.addVideoContent(22, videos)

        assertThat(result).isEqualTo(videos)
        verify { videoContentManager.newContent(ContentKey.TEAM, 22, videos) }
    }

    @Test
    fun shouldGiveMeTheTeamPlayers() {
        val player = PlayerSummary(
            "player-x",
            22,
            "1.80",
            "78",
            "Goalkeeper",
            0,
            1,
            0,
            3,
            1
        )

        every { apidata.teamSquad(33) } returns mapOf(1 to ClientTeamDetails.mockPlayersResponse)
        every { teamSquadManipulation.teamSquadSummary(mapOf(1 to ClientTeamDetails.mockPlayersResponse)) } returns
                listOf(player)

        val result: List<PlayerSummary> = underTest.teamPlayers(33)

        assertThat(result[0]).isEqualTo(player)

        verify { apidata.teamSquad(33) }
        verify { teamSquadManipulation.teamSquadSummary(mapOf(1 to ClientTeamDetails.mockPlayersResponse)) }
    }

    @Test
    fun shouldFetchTheTeamFixture() {
        every { livedata.allLiveMatches() } returns ClientLiveMatches.liveFixtures
        every { matchesData.previousAndUpcomingMatches(1) } returns mapOf()
        every { fixturesManipulation.extractTeamFixture(any(), any()) } returns TeamFixture(listOf(), listOf())

        val result = underTest.teamFixture(1)

        assertThat(result).isNotNull

        verify { matchesData.previousAndUpcomingMatches(1) }
        verify { fixturesManipulation.extractTeamFixture(any(), any()) }
    }

    @Test
    fun shouldFetchTheCurrentTeamPlayers() {
        every { apidata.currentTeamSquad(1) } returns ClientSquadPlayers.squadPlayers

        val result = underTest.teamSquad(1)
        assertThat(result.size).isEqualTo(2)
        assertThat(result[0]).isInstanceOf(TeamPlayer::class.java)

        verify { apidata.currentTeamSquad(1) }
    }

    @Test
    fun shouldFetchTeamLeagues() {
        every { apidata.teamLeagues(50, any()) } returns ClientTeamLeaguesParticipation.participation

        val result = underTest.teamLeagues(50)
        assertThat(result).isNotEmpty

        verify { apidata.teamLeagues(50, any()) }
    }


}


