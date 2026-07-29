package match.insights.controller

import com.fasterxml.jackson.databind.ObjectMapper
import match.insights.TestCorsPropsConfig

import com.ninjasquad.springmockk.MockkBean
import io.mockk.just
import io.mockk.every
import io.mockk.runs
import io.mockk.verify
import match.insights.request.VotingPoll
import match.insights.service.PollsService
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
@WebMvcTest(PollsController::class)
class PollsControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var pollsService: PollsService

    val objectMapper = ObjectMapper()

    @Test
    fun shouldSaveMatchWinnerVote() {
        every { pollsService.vote(any()) } just runs

        val votingPoll = VotingPoll(
            pollKey = "match-winner",
            fixtureId = 234233,
            optionName = "home"
        )

        val response = mvc.perform(
            post("/api/polls/vote")
                .content(objectMapper.writeValueAsString(votingPoll))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.CREATED.value())

        verify { pollsService.vote(any()) }
    }


    @Test
    fun shouldGetAvailablePolls() {
        every { pollsService.availablePolls() } returns listOf()

        val response = mvc.perform(
            get("/api/polls/available")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { pollsService.availablePolls() }
    }


}