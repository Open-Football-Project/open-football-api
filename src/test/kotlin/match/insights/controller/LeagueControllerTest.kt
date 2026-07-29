package match.insights.controller

import com.fasterxml.jackson.databind.ObjectMapper
import match.insights.TestCorsPropsConfig
import match.insights.service.LeagueService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import match.insights.model.VideoContent
import match.insights.request.VideoContentRequest
import match.insights.response.ArgSpecial
import match.insights.response.LeagueFixture
import match.insights.response.LeagueInfo
import match.insights.response.LeagueRankings
import match.insights.response.LeaguesGroups
import match.insights.service.ArgentinaService
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

@WebMvcTest(LeagueController::class)
@Import(TestCorsPropsConfig::class)
class LeagueControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var leagueService: LeagueService

    @MockkBean
    private lateinit var argentinaService: ArgentinaService

    val objectMapper = ObjectMapper()

    @Test
    fun shouldGetLeagueInfo() {
        every { leagueService.leagueInfo(any()) } returns
                LeagueInfo(id = 1, season = 2025, group = emptyList(), videos = emptySet())

        val response = mvc.perform(
            get("/api/league/standing/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { leagueService.leagueInfo(any()) }
    }

    @Test
    fun shouldGetAllLeagues() {
        every { leagueService.allLeagues() } returns
                LeaguesGroups(listOf(), listOf(), listOf())

        val response = mvc.perform(
            get("/api/league/all")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { leagueService.allLeagues() }
    }

    @Test
    fun shouldGetTheLeagueFixture() {
        every { leagueService.leagueSeasonFixture(39) } returns
                LeagueFixture(
                    listOf(), 1, 1
                )

        val response = mvc.perform(
            get("/api/league/fixture/39")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { leagueService.leagueSeasonFixture(39) }
    }

    @Test
    fun shouldGetTheLeagueRankings() {
        every { leagueService.leagueRankings(39) } returns
                LeagueRankings()

        val response = mvc.perform(
            get("/api/league/rankings/39")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { leagueService.leagueRankings(39) }
    }

    @Test
    fun shouldGetTheLeagueTeams() {
        every { leagueService.currentYearTeams(39) } returns
                listOf()

        val response = mvc.perform(
            get("/api/league/teams/39")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { leagueService.currentYearTeams(39) }
    }


    @Test
    fun shouldGetArgentinaSpecialTables() {
        every { argentinaService.argSpecialTables() } returns ArgSpecial(emptyList(), emptyList())

        val response = mvc.perform(
            get("/api/league/arg/special")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { argentinaService.argSpecialTables() }
    }


    @Test
    fun shouldAddVideosToALeague() {
        val urls = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { leagueService.addVideoContent(39, urls) } returns urls

        val response = mvc.perform(
            post("/api/league/39/video")
                .content(objectMapper.writeValueAsString(VideoContentRequest(urls)))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.CREATED.value())

        verify { leagueService.addVideoContent(39, urls) }
    }


}