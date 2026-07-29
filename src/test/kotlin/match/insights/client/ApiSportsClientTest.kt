package match.insights.client

import match.insights.clientData.ApiPagingResponse
import match.insights.clientData.ClientTeamStatistics
import match.insights.clientData.CoachResponse
import match.insights.clientData.LineupTeam
import match.insights.clientData.LiveFixtureResponse
import match.insights.clientData.LiveTeamStats
import match.insights.clientData.Paging
import match.insights.clientData.PlayerResponse
import match.insights.clientData.PlayerTransfer
import match.insights.clientData.PlayerTrophy
import match.insights.clientData.RankingPlayerStats
import match.insights.clientData.SquadPlayer
import match.insights.clientData.TeamLeagueParticipation
import match.insights.clientData.TeamResponse
import match.insights.data.client.raw.ClientCurrentSquad
import match.insights.data.client.raw.ClientLiveOddsData
import match.insights.data.client.raw.ClientRankings
import match.insights.data.client.raw.ClientRawData
import match.insights.data.client.raw.ClientRawLineups
import match.insights.data.client.raw.ClientRawPlayerInfo
import match.insights.data.client.raw.ClientRawPlayerTrophies
import match.insights.data.client.raw.ClientRawStatistics
import match.insights.data.client.raw.ClientRawTeamStats
import match.insights.data.client.raw.ClientRawTeamTransfers
import match.insights.data.client.raw.ClientRawTransfers
import match.insights.data.client.raw.LeaguesRawData
import match.insights.data.client.raw.LiveFixtureWithEvents
import match.insights.data.client.raw.RawLeagueTeams
import match.insights.data.client.raw.RawTeamLeagueParticipations
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import java.time.Duration

class ApiSportsClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var restClient: RestClient
    private lateinit var underTest: ApiSportsClient

    private fun fastRetryRegistry(maxAttempts: Int = 3): RetryRegistry = RetryRegistry.of(
        RetryConfig.custom<Any>()
            .maxAttempts(maxAttempts)
            .waitDuration(Duration.ofMillis(10))
            .build()
    )

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString()

        restClient = RestClient.builder().baseUrl(baseUrl).build()

        underTest = ApiSportsClient(restClient, RateLimiterRegistry.ofDefaults())
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should fetch Today Matches`() {

        val mockJson = ClientRawData.todayMatches

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchMatches("/fixtures?date=2025-07-27")

        assertThat(result.first().teams).isNotNull
        assertThat(result.first().fixture).isNotNull
        assertThat(result.first().league).isNotNull
        assertThat(result.first().venue).isNotNull
        assertThat(result.first().goals).isNotNull
        assertThat(result.first().score).isNotNull

    }

    @Test
    fun `should fetch league standings`() {
        val mockJson = LeaguesRawData.leagueStandingsMock

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchLeagueInfo("/standings?league=39&season=2025")

        assertThat(result?.id).isEqualTo(128)
        assertThat(result?.season).isEqualTo(2024)
        assertThat(result?.standings?.size).isGreaterThan(0)

    }

    @Test
    fun `should fetch all leagues`() {
        val mockJson = LeaguesRawData.allLeagues

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchAllLeagues("/leagues")

        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].country.name).isEqualTo("England")
        assertThat(result[0].league.id).isEqualTo(39)
        assertThat(result[0].league.name).isEqualTo("Premier League")

    }

    @Test
    fun `should fetch match details`() {
        val mockJson = ClientRawData.matchDetails

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchMatchDetails("/fixture?id=2025")

        assertThat(result.teams).isNotNull
        assertThat(result.fixture).isNotNull
        assertThat(result.league).isNotNull
        assertThat(result.venue).isNotNull
    }


    @Test
    fun `should fetch the odds`() {
        val mockJson = ClientRawData.oddsResponse

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchFixtureOdds("/odds?fixture=1326874")

        assertThat(result[0].bookmakers[0].bets[0].name).isEqualTo("Match Winner")
        assertThat(result[0].bookmakers[0].bets[0].values).isNotEmpty
        assertThat(result[0].bookmakers[0].bets[1].name).isEqualTo("Odd/Even - First Half")
        assertThat(result[0].bookmakers[0].bets[1].values).isNotEmpty
    }

    @Test
    fun `should fetch match Events`() {
        val mockJson = ClientRawData.matchEvents

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchMatchEvents("/fixtures/events?fixture=${12124}")

        assertThat(result).isNotEmpty

    }

    @Test
    fun `should fetch Team Details`() {
        val mockJson = ClientRawData.teamDetailsRaw

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: TeamResponse = underTest.fetchTeamDetails("/teams?id=${2431}")

        assertThat(result.team).isNotNull
        assertThat(result.venue).isNotNull

    }

    @Test
    fun `should fetch Coach Details`() {
        val mockJson = ClientRawData.coachResponse

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<CoachResponse> = underTest.fetchCoachDetails("/coachs?team=33")

        assertThat(result[0].name).isEqualTo("Erik ten Hag")

    }

    @Test
    fun `should fetch handle it when the response has not data`() {
        val mockJson = ClientRawData.coachResponse

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<CoachResponse> = underTest.fetchCoachDetails("/coachs?team=33")

        assertThat(result[0].name).isEqualTo("Erik ten Hag")

    }

    @Test
    fun `should fetch Squad`() {
        val mockJson = ClientRawData.playersResponse


        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: ApiPagingResponse<List<PlayerResponse>> =
            underTest.fetchPlayers("/players?team=$33&season=2025&page=1")

        assertThat(result.response.size).isEqualTo(2)
        assertThat(result.paging).isEqualTo(Paging(1, 1))

    }

    @Test
    fun `should fetch live fixtures with events`() {
        val mockJson = ClientCurrentSquad.mockJson


        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<SquadPlayer> =
            underTest.fetchCurrentSquad("/players/squad?team=435")

        assertThat(result.size).isEqualTo(3)
        assertThat(result[0].id).isEqualTo(6492)
        assertThat(result[0].name).isEqualTo("J. Ledesma")
        assertThat(result[1].id).isEqualTo(2463)
        assertThat(result[1].name).isEqualTo("F. Armani")
        assertThat(result[2].id).isEqualTo(9933)
        assertThat(result[2].name).isEqualTo("M. Borja")

    }

    @Test
    fun `returns an empty list when api-sports has no squad data for the team`() {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("""{"response": []}""")
                .addHeader("Content-Type", "application/json")
        )

        val result: List<SquadPlayer> = underTest.fetchCurrentSquad("/players/squad?team=999999")

        assertThat(result).isEmpty()
    }

    @Test
    fun `should fetch current team Squad`() {
        val mockJson = LiveFixtureWithEvents.response


        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<LiveFixtureResponse> =
            underTest.fetchLiveFixtures("/fixtures?live=all")

        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun `should fetch live fixtures when an event detail is missing`() {
        val mockJson = LiveFixtureWithEvents.responseWithNullDetail

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<LiveFixtureResponse> =
            underTest.fetchLiveFixtures("/fixtures?live=all")

        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].events[0].detail).isNull()
    }

    @Test
    fun `should fetch live match statistics`() {
        val mockJson = ClientRawStatistics.mockStatisticsJson


        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<LiveTeamStats> =
            underTest.fetchLiveStatistics("/fixtures/statistics?fixture=1457376")

        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun `should fetch live lineups`() {
        val mockJson = ClientRawLineups.mockLineupJson


        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<LineupTeam> =
            underTest.fetchLiveLineups("/fixtures/lineups?fixture=1457376")

        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun `should fetch league ranking`() {
        val mockJson = ClientRankings.topScorers

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<RankingPlayerStats> =
            underTest.fetchLeagueRanking("/players/topscorers?league=128&season=2025")

        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun `should fetch player trophies`() {
        val mockJson = ClientRawPlayerTrophies.playerTrophies

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<PlayerTrophy> =
            underTest.fetchPlayerTrophies("/trophies?player=142")

        assertThat(result.size).isEqualTo(5)
    }

    @Test
    fun `should fetch player transfers`() {
        val mockJson = ClientRawTransfers.rawTransfers

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<PlayerTransfer> =
            underTest.fetchTransfers("/transfers?player=142")

        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun `should fetch team transfers`() {
        val mockJson = ClientRawTeamTransfers.teamTransfers

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<PlayerTransfer> =
            underTest.fetchTransfers("/transfers?team=435")

        assertThat(result.size).isEqualTo(3)
    }

    @Test
    fun `should fetch team season stats`() {
        val mockJson = ClientRawTeamStats.teamStats

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: ClientTeamStatistics =
            underTest.fetchTeamStats("/teams/statistics?team=435")

        assertThat(result.league).isNotNull
        assertThat(result.team).isNotNull
        assertThat(result.fixtures).isNotNull
    }

    @Test
    fun `should fetch league teams`() {
        val mockJson = RawLeagueTeams.leagueTeams

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<TeamResponse> =
            underTest.fetchLeagueTeams("teams?league=128&season=2025")

        assertThat(result).isNotEmpty
    }

    @Test
    fun `should fetch team leagues participation list`() {
        val mockJson = RawTeamLeagueParticipations.teamLeagueParticipations

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result: List<TeamLeagueParticipation> =
            underTest.fetchTeamLeagues("/leagues?team=435&season=2024")

        assertThat(result).isNotEmpty
    }

    @Test
    fun `should fetch player Info`() {
        val mockJson = ClientRawPlayerInfo.playerInfoResponse

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchPlayerInfo("/player?id=1234&season=2025")

        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].player).isNotNull
        assertThat(result[0].statistics).isNotEmpty

    }

    @Test
    fun `should fetch player info even when a statistics entry has no team data (real payload for player 340124)`() {
        val mockJson = ClientRawPlayerInfo.playerInfoResponseWithMissingTeamData

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchPlayerInfo("/player?id=340124&season=2025")

        assertThat(result).hasSize(1)
        assertThat(result[0].statistics).hasSize(2)
        assertThat(result[0].statistics[0].team.id).isEqualTo(649)
        assertThat(result[0].statistics[0].team.name).isEqualTo("HJK Helsinki")
        assertThat(result[0].statistics[1].team.id).isNull()
        assertThat(result[0].statistics[1].team.name).isNull()
    }

    @Test
    fun `should fetch player info even when player id, player name, or league name are missing`() {
        val mockJson = ClientRawPlayerInfo.playerInfoResponseWithMissingIdentityFields

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchPlayerInfo("/player?id=2273&season=2025")

        assertThat(result).hasSize(1)
        assertThat(result[0].player.id).isNull()
        assertThat(result[0].player.name).isNull()
        assertThat(result[0].statistics[0].league.name).isNull()
    }

    @Test
    fun `should fetch live fixture odds`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(ClientLiveOddsData.liveOddsResponse)
                .addHeader("Content-Type", "application/json")
        )

        val result = underTest.fetchLiveFixtureOdds("/odds/live?fixture=1539007")

        assertThat(result).hasSize(1)
        assertThat(result[0].odds).hasSize(3)

        val fulltimeResult = result[0].odds.find { it.id == 59 }!!
        assertThat(fulltimeResult.name).isEqualTo("Fulltime Result")
        assertThat(fulltimeResult.values).hasSize(3)
        assertThat(fulltimeResult.values[0].value).isEqualTo("Home")
        assertThat(fulltimeResult.values[0].odd).isEqualTo("1.615")
        assertThat(fulltimeResult.values[0].suspended).isFalse()


    }

    @Test
    fun `throttles every endpoint through one shared rate limiter, rejecting once the configured budget is exhausted`() {
        val restrictiveRegistry = RateLimiterRegistry.of(
            RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build()
        )
        val restrictedClient = ApiSportsClient(restClient, restrictiveRegistry)
        val mockJson = ClientRawData.todayMatches
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        restrictedClient.fetchMatches("/fixtures?date=2025-07-27")

        assertThatThrownBy { restrictedClient.fetchTeamDetails("/teams?id=${2431}") }
            .isInstanceOf(RequestNotPermitted::class.java)
    }


    @Test
    fun `retries a 429 and succeeds once a later attempt returns 200`() {
        val client = ApiSportsClient(restClient, RateLimiterRegistry.ofDefaults(), fastRetryRegistry())
        val mockJson = ClientRawData.todayMatches

        mockWebServer.enqueue(MockResponse().setResponseCode(429))
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(mockJson).addHeader("Content-Type", "application/json")
        )

        val result = client.fetchMatches("/fixtures?date=2025-07-27")

        assertThat(result).isNotEmpty()
        assertThat(mockWebServer.requestCount).isEqualTo(2)
    }

    @Test
    fun `retries a non-429 failure too, since any exception is retried`() {
        val client = ApiSportsClient(restClient, RateLimiterRegistry.ofDefaults(), fastRetryRegistry())

        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(ClientRawData.todayMatches)
                .addHeader("Content-Type", "application/json")
        )

        val result = client.fetchMatches("/fixtures?date=2025-07-27")

        assertThat(result).isNotEmpty()
        assertThat(mockWebServer.requestCount).isEqualTo(2)
    }

    @Test
    fun `gives up and throws after exhausting all retries on a repeatedly failing call`() {
        val client = ApiSportsClient(restClient, RateLimiterRegistry.ofDefaults(), fastRetryRegistry(maxAttempts = 3))

        repeat(3) { mockWebServer.enqueue(MockResponse().setResponseCode(429)) }

        assertThatThrownBy { client.fetchMatches("/fixtures?date=2025-07-27") }
            .isInstanceOf(HttpClientErrorException.TooManyRequests::class.java)

        assertThat(mockWebServer.requestCount).isEqualTo(3)
    }
}