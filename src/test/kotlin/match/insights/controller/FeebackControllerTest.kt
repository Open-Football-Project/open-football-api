package match.insights.controller

import com.fasterxml.jackson.databind.ObjectMapper
import match.insights.TestCorsPropsConfig
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import match.insights.service.FeedbackService
import io.mockk.just
import io.mockk.runs
import match.insights.request.FeedbackRequest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@Import(TestCorsPropsConfig::class)
@WebMvcTest(FeedbackController::class)
class FeebackControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var feedbackService: FeedbackService

    val objectMapper = ObjectMapper()

    @Test
    fun shouldHandleNewPostedFeedback() {
        every { feedbackService.handleFeedback(any()) } just runs

        val response = mvc.perform(
            post("/api/feedback/new")
                .content(objectMapper.writeValueAsString(aFeedbackRequest()))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.CREATED.value())

        verify { feedbackService.handleFeedback(any()) }
    }


    private fun aFeedbackRequest(
        favoriteTeam: String = "Barcelona",
        league: String = "La Liga",
        liked: String = "Stats",
        improvements: String = "UI",
        wantsAndroidBeta: Boolean = false,
        googleEmail: String? = null
    ) = FeedbackRequest(
        favoriteTeam = favoriteTeam,
        league = league,
        liked = liked,
        improvements = improvements,
        wantsAndroidBeta = wantsAndroidBeta,
        googleEmail = googleEmail
    )
}