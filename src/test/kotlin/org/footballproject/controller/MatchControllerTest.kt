package org.footballproject.controller

import org.footballproject.TestCorsPropsConfig
import org.footballproject.data.client.ClientMatchResponseData
import org.footballproject.response.MatchDetails
import org.footballproject.service.MatchService
import com.ninjasquad.springmockk.MockkBean
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.verify
import org.footballproject.data.client.ClientEventsData
import org.footballproject.data.client.ClientLineupResponseData
import org.footballproject.model.VideoContent
import org.footballproject.request.VideoContentRequest
import org.footballproject.response.MatchEvent
import org.footballproject.response.TeamsLineups
import org.footballproject.response.TwoTeamsStatistics

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
@WebMvcTest(MatchController::class)
class MatchControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var matchService: MatchService

    val objectMapper = ObjectMapper()

    @Test
    fun shouldGetMatches() {
        every { matchService.getMatchesByDate("2024-11-11") } returns listOf()

        val response = mvc.perform(
            get("/api/matches?date=2024-11-11")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { matchService.getMatchesByDate("2024-11-11") }
    }

    @Test
    fun shouldGetMatchDetails() {
        every { matchService.getMatchDetails(any()) } returns
                MatchDetails.fromResponseData(
                    ClientMatchResponseData.matchResponse,
                    setOf(123456),
                    setOf(
                        VideoContent(
                            "https://www.youtube.com/watch?v=abc123",
                            "spanish-label",
                            "en-label",
                            "22/06/1990"
                        )
                    )
                )

        val response = mvc.perform(
            get("/api/matches/1234/details")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { matchService.getMatchDetails(any()) }
    }


    @Test
    fun shouldGetTheMatchStatistics() {
        every { matchService.matchStats(any()) } returns
                TwoTeamsStatistics()


        val response = mvc.perform(
            get("/api/matches/stats/243422")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { matchService.matchStats(any()) }
    }

    @Test
    fun shouldGetTheTeamsLineups() {
        every { matchService.matchLineups(any()) } returns
                TeamsLineups.fromClientData(ClientLineupResponseData.mockLineups)


        val response = mvc.perform(
            get("/api/matches/lineups/243422")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { matchService.matchLineups(any()) }
    }

    @Test
    fun shouldGetTheMatchEvents() {
        every { matchService.matchEvents(any()) } returns
                ClientEventsData.mockEvents.map {
                    MatchEvent.fromEvent(it)
                }


        val response = mvc.perform(
            get("/api/matches/events/243422")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        verify { matchService.matchEvents(any()) }
    }

    @Test
    fun shouldAddVideosToAMatch() {
        val urls = setOf(
            VideoContent(
                "https://www.youtube.com/watch?v=abc123",
                "spanish-label",
                "en-label",
                "22/06/1990"
            )
        )
        every { matchService.addVideoContent(1089175, urls) } returns urls

        val response = mvc.perform(
            post("/api/matches/1089175/video")
                .content(objectMapper.writeValueAsString(VideoContentRequest(urls)))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.CREATED.value())

        verify { matchService.addVideoContent(1089175, urls) }
    }

}