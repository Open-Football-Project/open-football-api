package org.footballproject.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.footballproject.TestCorsPropsConfig
import org.footballproject.model.LiveChartPoint
import org.footballproject.response.BetMarketInfo
import org.footballproject.response.BetOddsPoint
import org.footballproject.response.FixtureChartsResponse
import org.footballproject.response.LiveChartableMatch
import org.footballproject.service.LiveChartsBetsService
import org.footballproject.service.LiveChartsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import java.time.Instant

@WebMvcTest(LiveChartsController::class)
@Import(TestCorsPropsConfig::class)
class LiveChartsControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var liveChartsService: LiveChartsService

    @MockkBean
    private lateinit var liveChartsBetsService: LiveChartsBetsService

    @Test
    fun shouldReturnTheIndicatorsMap() {
        val point = LiveChartPoint(23, 71, Instant.parse("2026-06-22T10:00:00Z"))
        every { liveChartsService.fixtureIndicators(233) } returns mapOf("momentum" to listOf(point))

        val response = mvc.perform(
            get("/api/charts/all/233")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).contains("\"momentum\"")
        assertThat(response.contentAsString).contains("\"value\":71")

        verify { liveChartsService.fixtureIndicators(233) }
    }

    @Test
    fun shouldReturnAllFixturesWithTheIndicatorsMap() {
        val point = LiveChartPoint(23, 71, Instant.parse("2026-06-22T10:00:00Z"))
        every { liveChartsService.allFixturesIndicators() } returns listOf(
            FixtureChartsResponse(
                fixtureId = 1232,
                homeTeamName = "team a",
                awayTeamName = "team b",
                indicators = mapOf("momentum" to listOf(point))
            )
        )


        val response = mvc.perform(
            get("/api/charts/all")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).contains("\"team a\"")
        assertThat(response.contentAsString).contains("\"team b\"")

        verify { liveChartsService.allFixturesIndicators() }
    }

    @Test
    fun shouldReturnTheTrackableLiveMatches() {
        val match = LiveChartableMatch(fixtureId = 233, homeTeamName = "Home FC", awayTeamName = "Away FC")
        every { liveChartsService.trackableLiveMatches() } returns listOf(match)

        val response = mvc.perform(
            get("/api/charts/matches")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).contains("\"homeTeamName\":\"Home FC\"")
        assertThat(response.contentAsString).contains("\"awayTeamName\":\"Away FC\"")

        verify { liveChartsService.trackableLiveMatches() }
    }

    @Test
    fun shouldReturnBetMarketsChunked() {
        val history = mapOf("Home" to listOf(BetOddsPoint(23, "1.666", Instant.parse("2026-06-22T10:00:00Z"))))
        every { liveChartsBetsService.getAllLiveMarkets(1539007) } returns listOf(
            listOf(BetMarketInfo(59, "fulltime_result", history))
        )

        val response = mvc.perform(
            get("/api/charts/markets/1539007")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).contains("\"fulltime_result\"")
        assertThat(response.contentAsString).contains("\"1.666\"")

        verify { liveChartsBetsService.getAllLiveMarkets(1539007) }
    }

    @Test
    fun shouldReturnTheTrackedOddsFixtures() {
        val match = LiveChartableMatch(fixtureId = 1539007, homeTeamName = "Netherlands", awayTeamName = "Sweden")
        every { liveChartsBetsService.trackedFixtures() } returns listOf(match)

        val response = mvc.perform(
            get("/api/charts/odds/fixtures")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).contains("\"homeTeamName\":\"Netherlands\"")
        assertThat(response.contentAsString).contains("\"awayTeamName\":\"Sweden\"")

        verify { liveChartsBetsService.trackedFixtures() }
    }

    @Test
    fun shouldReturnTheActiveLeagueIds() {
        every { liveChartsService.activeLeagueIds() } returns listOf(39, 140)

        val response = mvc.perform(
            get("/api/charts/leagues")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).isEqualTo("[39,140]")

        verify { liveChartsService.activeLeagueIds() }
    }
}
