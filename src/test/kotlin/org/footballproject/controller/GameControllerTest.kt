package org.footballproject.controller

import org.footballproject.TestCorsPropsConfig
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.footballproject.response.game.GuessThePlayerGameData
import org.footballproject.response.game.GuessTheTeamGameData
import org.footballproject.service.PlayerGameService
import org.footballproject.service.TeamGameService
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
@WebMvcTest(GameController::class)
class GameControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var playerGameService: PlayerGameService


    @MockkBean
    private lateinit var teamGameService: TeamGameService

    @Test
    fun shouldGetAnewGuessThePlayerGame() {
        every { playerGameService.generatePlayerGame(any()) } returns GuessThePlayerGameData()

        val response = mvc.perform(
            get("/api/game/435/player")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { playerGameService.generatePlayerGame(any()) }
    }


    @Test
    fun shouldGetAnewGuessTheTeamGame() {
        every { teamGameService.newTeamGame(any()) } returns GuessTheTeamGameData()

        val response = mvc.perform(
            get("/api/game/128/team")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { teamGameService.newTeamGame(any()) }
    }
}