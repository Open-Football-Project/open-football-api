package match.insights.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import match.insights.TestCorsPropsConfig
import match.insights.service.TodayPlayersService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@Import(TestCorsPropsConfig::class)
@WebMvcTest(AdminController::class)
@TestPropertySource(properties = ["admin.trigger-key=test-secret"])
class AdminControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var todayPlayersService: TodayPlayersService

    @Test
    fun `triggers the capture job when the correct admin key is supplied`() {
        every { todayPlayersService.captureTodayPlayers() } returns Unit

        val response = mvc.perform(
            post("/api/admin/today-players/capture").header("X-Admin-Key", "test-secret")
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        verify { todayPlayersService.captureTodayPlayers() }
    }

    @Test
    fun `rejects the request with 403 when the admin key is wrong`() {
        val response = mvc.perform(
            post("/api/admin/today-players/capture").header("X-Admin-Key", "wrong-key")
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.FORBIDDEN.value())
        verify(exactly = 0) { todayPlayersService.captureTodayPlayers() }
    }

    @Test
    fun `rejects the request with 403 when no admin key header is supplied`() {
        val response = mvc.perform(
            post("/api/admin/today-players/capture")
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.FORBIDDEN.value())
        verify(exactly = 0) { todayPlayersService.captureTodayPlayers() }
    }
}
