package match.insights.controller

import match.insights.TestCorsPropsConfig
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import match.insights.data.client.raw.ClientRankingsData
import match.insights.model.RankingKey
import match.insights.response.LeagueRankingPlayer
import match.insights.service.RankingsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Import(TestCorsPropsConfig::class)
@WebMvcTest(RankingController::class)
class RankingControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var rankingsService: RankingsService

    @Test
    fun shouldGetTopScorers() {
        every { rankingsService.leagueRanking(RankingKey.SCORERS, 128) } returns
                ClientRankingsData.topScorers.map { LeagueRankingPlayer.fromClientResponse(it) }

        val response = mvc.perform(
            get("/api/ranking/league?key=SCORERS&leagueId=128")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())

        verify { rankingsService.leagueRanking(RankingKey.SCORERS, 128) }
    }
}