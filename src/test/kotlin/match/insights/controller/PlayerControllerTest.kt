package match.insights.controller

import com.fasterxml.jackson.databind.ObjectMapper
import match.insights.TestCorsPropsConfig

import com.ninjasquad.springmockk.MockkBean

import io.mockk.every
import io.mockk.verify
import match.insights.model.VideoContent
import match.insights.request.VideoContentRequest
import match.insights.response.PlayerHistory
import match.insights.response.PlayerMainInfo
import match.insights.response.FixtureTodayPlayers

import match.insights.service.PlayerService
import match.insights.service.TodayPlayersService

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@Import(TestCorsPropsConfig::class)
@WebMvcTest(PlayerController::class)
class PlayerControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var playerHistoryService: PlayerService

    @MockkBean
    private lateinit var todayPlayersService: TodayPlayersService

    val objectMapper = ObjectMapper()

    @Test
    fun shouldGetPlayerHistory() {
        every { playerHistoryService.playerHistory(any()) } returns PlayerHistory(null, emptyList(), emptyList())

        val response = mvc.perform(
            get("/api/player/history/45").contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { playerHistoryService.playerHistory(any()) }
    }

    @Test
    fun shouldGetPlayerFullProfile() {
        every { playerHistoryService.fullPlayerInfo(any()) } returns PlayerMainInfo(videos = emptySet())

        val response = mvc.perform(
            get("/api/player/45").contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { playerHistoryService.fullPlayerInfo(any()) }
    }

    @Test
    fun shouldAddVideosToATeam() {
        val urls = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { playerHistoryService.addVideoContent(19531, urls) } returns urls

        val response = mvc.perform(
            post("/api/player/19531/video")
                .content(objectMapper.writeValueAsString(VideoContentRequest(urls)))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.CREATED.value())

        verify { playerHistoryService.addVideoContent(19531, urls) }
    }

    @Test
    fun shouldGetTodayPlayersTrackedLeagues() {
        every { todayPlayersService.trackedLeagueIds() } returns listOf(39, 140)

        val response = mvc.perform(
            get("/api/player/today-players/leagues").contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { todayPlayersService.trackedLeagueIds() }
    }

    @Test
    fun shouldGetAvailableTodayPlayersFixtureIds() {
        every { todayPlayersService.availableFixtureIds() } returns listOf(1576804)

        val response = mvc.perform(
            get("/api/player/today-players/fixtures").contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { todayPlayersService.availableFixtureIds() }
    }

    @Test
    fun shouldGetAllTodayPlayersFixtures() {
        every { todayPlayersService.allFixtures() } returns listOf(
            FixtureTodayPlayers(
                fixtureId = 1576804,
                leagueId = 1,
                leagueName = "World Cup",
                homeTeamName = "Argentina",
                awayTeamName = "Egypt",
                home = emptyMap(),
                away = emptyMap()
            )
        )

        val response = mvc.perform(
            get("/api/player/today-players").contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { todayPlayersService.allFixtures() }
    }
}