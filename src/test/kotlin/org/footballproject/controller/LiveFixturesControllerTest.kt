package org.footballproject.controller

import org.footballproject.TestCorsPropsConfig
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify

import org.footballproject.service.LiveFixturesService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@WebMvcTest(LiveFixturesController::class)
@Import(TestCorsPropsConfig::class)
class LiveFixturesControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var liveFixturesService: LiveFixturesService


    @Test
    fun shouldGetStreamLiveMatches() {
        every { liveFixturesService.streamLiveMatches() } returns
                SseEmitter()

        val response = mvc.perform(
            get("/api/live/all")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { liveFixturesService.streamLiveMatches() }
    }

    @Test
    fun shouldGetLiveMatchesEvents() {
        every { liveFixturesService.getLiveMatches() } returns emptyList()

        val response = mvc.perform(
            get("/api/live/events")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { liveFixturesService.getLiveMatches() }
    }

}