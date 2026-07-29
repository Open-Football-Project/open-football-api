package org.footballproject.controller

import org.footballproject.TestCorsPropsConfig
import org.footballproject.model.OddFeeling
import org.footballproject.response.OddsWinnerFeeling
import org.footballproject.response.ValueBetsResponse
import org.footballproject.service.OddsService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Import(TestCorsPropsConfig::class)
@WebMvcTest(OddsController::class)
class OddsControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var oddsService: OddsService

    @Test
    fun shouldGetTheBets() {
        every { oddsService.fetchAllOdds(any()) } returns listOf()

        val response = mvc.perform(
            get("/api/odds/2142")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { oddsService.fetchAllOdds(any()) }
    }

    @Test
    fun shouldGetWinnerFeeling() {
        every { oddsService.oddsWinnerFeeling(any()) } returns
                OddsWinnerFeeling(
                    OddFeeling.STRONG.value,
                    OddFeeling.WEAK.value,
                    OddFeeling.WEAK.value
                )

        val response = mvc.perform(
            get("/api/odds/feeling/winner/2142")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { oddsService.oddsWinnerFeeling(any()) }
    }

    @Test
    fun shouldGetValueBets() {
        every { oddsService.fetchValueBets(any()) } returns ValueBetsResponse(markets = emptyList())

        val response = mvc.perform(
            get("/api/odds/value-bets/2142")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        verify { oddsService.fetchValueBets(any()) }
    }
}